package com.fooddelivery.restaurant;

import com.fooddelivery.common.client.OrganisationServiceClient;
import com.fooddelivery.common.dto.organisation.MembershipDto;
import com.fooddelivery.common.enums.*;
import com.fooddelivery.restaurant.entity.*;
import com.fooddelivery.restaurant.repository.*;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.*;
import java.time.ZoneId;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest(properties={"spring.cloud.config.enabled=false", "spring.flyway.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:o2_query_count;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.generate_statistics=true"})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes=RestaurantAccessQueryCountTest.Config.class)
@Transactional(propagation=Propagation.NOT_SUPPORTED)
class RestaurantAccessQueryCountTest {
    @TestConfiguration @EnableAutoConfiguration
    @EntityScan(basePackageClasses=Brand.class)
    @EnableJpaRepositories(basePackageClasses=BrandRepository.class)
    static class Config {
        @Bean RestaurantOnboardingService onboarding(BrandRepository brands, OutletRepository outlets, OrganisationServiceClient orgs) {
            return new RestaurantOnboardingService(brands, outlets,
                    mock(com.fooddelivery.common.outbox.repository.OutboxEventRepository.class),
                    new com.fasterxml.jackson.databind.ObjectMapper(), mock(org.springframework.cache.CacheManager.class), orgs);
        }
    }
    @MockBean OrganisationServiceClient organisations;
    @Autowired RestaurantOnboardingService service;
    @Autowired BrandRepository brands;
    @Autowired OutletRepository outlets;
    @Autowired EntityManagerFactory factory;

    @Test void oneAndTwentyOrganisationsUseOneMembershipCallAndOneStatementWithTimingsLoaded() {
        UUID user = UUID.randomUUID();
        var memberships = new ArrayList<MembershipDto>();
        addOutlet(user, memberships);
        long one = statements(user, memberships, 1);
        for (int i=1; i<20; i++) { addOutlet(user, memberships); }
        long twenty = statements(user, memberships, 20);
        assertEquals(1, one);
        assertEquals(one, twenty, "Membership count must not introduce per-brand queries");
        verify(organisations, times(2)).getUserOrganisations(user);
    }

    private void addOutlet(UUID user, List<MembershipDto> memberships) {
        UUID orgId = UUID.randomUUID();
        Brand brand = brands.saveAndFlush(Brand.builder().id(UUID.randomUUID()).organisationId(orgId).name("Query brand").build());
        Outlet outlet = Outlet.builder().id(UUID.randomUUID()).brandId(brand.getId()).name("Query outlet")
                .cityId("BLR").timeZone(ZoneId.of("Asia/Kolkata")).isActive(true).build();
        OutletTiming timing = OutletTiming.builder().id(UUID.randomUUID()).outlet(outlet)
                .openingTime(java.time.LocalTime.MIN).closingTime(java.time.LocalTime.MAX).build();
        outlet.setTimings(new ArrayList<>(List.of(timing)));
        outlets.saveAndFlush(outlet);
        memberships.add(new MembershipDto(orgId, OrganisationStatus.ACTIVE, user, OrganisationRole.STAFF, MembershipStatus.ACTIVE));
    }
    private long statements(UUID user, List<MembershipDto> memberships, int count) {
        when(organisations.getUserOrganisations(user)).thenReturn(List.copyOf(memberships));
        var stats = factory.unwrap(SessionFactory.class).getStatistics(); stats.clear();
        var result = service.getOutletsForUser(user, OrganisationPermission.ORDERS_OPERATE);
        assertEquals(count, result.size());
        assertTrue(result.stream().allMatch(o -> o.getTimings().size()==1), "Timings must already be loaded");
        return stats.getPrepareStatementCount();
    }
}
