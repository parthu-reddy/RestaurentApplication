package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.client.OrganisationServiceClient;
import com.fooddelivery.common.dto.organisation.MembershipDto;
import com.fooddelivery.common.enums.*;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantMembershipsTest {
    private final UUID user = UUID.randomUUID();
    private final UUID org = UUID.randomUUID();
    private final AtomicLong nanos = new AtomicLong();
    private final OrganisationServiceClient client = mock(OrganisationServiceClient.class);
    private final RestaurantMemberships snapshots = new RestaurantMemberships(client, nanos::get);

    @Test void cacheHitsCannotExtendTheFiveSecondRevocationWindow() {
        var active = membership(OrganisationRole.MANAGER, MembershipStatus.ACTIVE, OrganisationStatus.ACTIVE);
        var removed = membership(OrganisationRole.MANAGER, MembershipStatus.REMOVED, OrganisationStatus.ACTIVE);
        when(client.getUserOrganisations(user)).thenReturn(List.of(active), List.of(removed));
        assertThat(snapshots.forUser(user)).containsExactly(active);
        advance(Duration.ofSeconds(4));
        assertThat(snapshots.forUser(user)).containsExactly(active);
        advance(Duration.ofSeconds(1));
        assertThat(snapshots.forUser(user)).containsExactly(removed);
        verify(client, times(2)).getUserOrganisations(user);
    }

    @Test void permissionAndStatusAreEvaluatedOnEachWarmRead() {
        when(client.getUserOrganisations(user)).thenReturn(List.of(
                membership(OrganisationRole.STAFF, MembershipStatus.ACTIVE, OrganisationStatus.ACTIVE)));
        OutletRepository outlets = mock(OutletRepository.class);
        var outlet = Outlet.builder().id(UUID.randomUUID()).brandId(UUID.randomUUID()).name("Fixture outlet").build();
        when(outlets.findByOrganisationIdIn(List.of(org))).thenReturn(List.of(outlet));
        RestaurantOnboardingService service = service(mock(BrandRepository.class), outlets);
        assertThat(service.getOutletsForUser(user, OrganisationPermission.ORDERS_OPERATE)).containsExactly(outlet);
        assertThat(service.getOutletsForUser(user, OrganisationPermission.EARNINGS_VIEW)).isEmpty();
        assertThat(service.getOutletsForUser(user, OrganisationPermission.MENU_MANAGE)).isEmpty();
        verify(client).getUserOrganisations(user);
        verify(outlets).findByOrganisationIdIn(List.of(org));
    }

    @Test void suspendedOrganisationCanBeViewedButCannotOperateFromTheSameSnapshot() {
        when(client.getUserOrganisations(user)).thenReturn(List.of(
                membership(OrganisationRole.OWNER, MembershipStatus.ACTIVE, OrganisationStatus.SUSPENDED)));
        BrandRepository brands = mock(BrandRepository.class);
        var brand = Brand.builder().id(UUID.randomUUID()).organisationId(org).name("Suspended fixture").build();
        when(brands.findAllByOrganisationIdIn(List.of(org))).thenReturn(List.of(brand));
        OutletRepository outlets = mock(OutletRepository.class);
        RestaurantOnboardingService service = service(brands, outlets);
        assertThat(service.getBrands(user)).containsExactly(brand);
        assertThat(service.getOutletsForUser(user, OrganisationPermission.ORDERS_OPERATE)).isEmpty();
        verify(client).getUserOrganisations(user);
        verifyNoInteractions(outlets);
    }

    @Test void expiredMembershipsAreNeverUsedAfterAnIdentityFailure() {
        when(client.getUserOrganisations(user))
                .thenReturn(List.of(membership(OrganisationRole.OWNER, MembershipStatus.ACTIVE, OrganisationStatus.ACTIVE)))
                .thenThrow(new IllegalStateException("Identity unavailable"))
                .thenReturn(List.of());
        assertThat(snapshots.forUser(user)).hasSize(1);
        advance(Duration.ofSeconds(5));
        RestaurantOnboardingService service = service(mock(BrandRepository.class), mock(OutletRepository.class));
        assertThatThrownBy(() -> service.getBrands(user)).isInstanceOfSatisfying(
                ResponseStatusException.class, error -> assertThat(error.getStatusCode().value()).isEqualTo(503));
        assertThat(service.getBrands(user)).isEmpty();
        verify(client, times(3)).getUserOrganisations(user);
    }

    @Test void nullResponsesAreNotCachedAndCannotGrantAccess() {
        when(client.getUserOrganisations(user)).thenReturn(null, List.of());
        RestaurantOnboardingService service = service(mock(BrandRepository.class), mock(OutletRepository.class));
        assertThatThrownBy(() -> service.getBrands(user)).isInstanceOf(ResponseStatusException.class);
        assertThat(service.getBrands(user)).isEmpty();
        verify(client, times(2)).getUserOrganisations(user);
    }

    @Test void cachedRowsAreAnImmutableSnapshotOfTheOriginalResponse() {
        var active = membership(OrganisationRole.STAFF, MembershipStatus.ACTIVE, OrganisationStatus.ACTIVE);
        var original = new ArrayList<>(List.of(active));
        when(client.getUserOrganisations(user)).thenReturn(original);
        var result = snapshots.forUser(user);
        original.clear();
        assertThat(snapshots.forUser(user)).containsExactly(active);
        assertThatThrownBy(() -> result.clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test void simultaneousColdReadsShareOneBulkIdentityCall() throws Exception {
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        when(client.getUserOrganisations(user)).thenAnswer(invocation -> {
            entered.countDown();
            assertThat(release.await(5, TimeUnit.SECONDS)).isTrue();
            return List.of(membership(OrganisationRole.STAFF, MembershipStatus.ACTIVE, OrganisationStatus.ACTIVE));
        });
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            var first = executor.submit(() -> snapshots.forUser(user));
            assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
            var others = new ArrayList<Future<List<MembershipDto>>>();
            for (int i = 0; i < 3; i++) others.add(executor.submit(() -> snapshots.forUser(user)));
            release.countDown();
            assertThat(first.get(5, TimeUnit.SECONDS)).hasSize(1);
            for (var other : others) assertThat(other.get(5, TimeUnit.SECONDS)).hasSize(1);
            verify(client).getUserOrganisations(user);
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    private MembershipDto membership(OrganisationRole role, MembershipStatus status, OrganisationStatus organisationStatus) {
        return new MembershipDto(org, organisationStatus, user, role, status);
    }
    private void advance(Duration duration) { nanos.addAndGet(duration.toNanos()); }
    private RestaurantOnboardingService service(BrandRepository brands, OutletRepository outlets) {
        return new RestaurantOnboardingService(brands, outlets, mock(OutboxEventRepository.class),
                new ObjectMapper(), mock(CacheManager.class), snapshots);
    }
}
