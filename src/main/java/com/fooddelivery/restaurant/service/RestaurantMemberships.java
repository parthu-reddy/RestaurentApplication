package com.fooddelivery.restaurant.service;

import com.fooddelivery.common.client.OrganisationServiceClient;
import com.fooddelivery.common.dto.organisation.MembershipDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Shares the short-lived membership snapshot across an account's brand and outlet reads. */
@Component
public class RestaurantMemberships {
    private final OrganisationServiceClient client;
    private final Cache<UUID, List<MembershipDto>> snapshots;

    @Autowired
    public RestaurantMemberships(OrganisationServiceClient client) {
        this(client, Ticker.systemTicker());
    }

    RestaurantMemberships(OrganisationServiceClient client, Ticker ticker) {
        this.client = client;
        snapshots = Caffeine.newBuilder().maximumSize(10_000)
                .expireAfterWrite(Duration.ofSeconds(5)).ticker(ticker).build();
    }

    public List<MembershipDto> forUser(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        // The loader is coalesced per user. Cache records, never a permission decision; neither
        // hits nor failed reloads extend the original five-second lifetime. No stale fallback.
        return snapshots.get(userId, id -> List.copyOf(Objects.requireNonNull(
                client.getUserOrganisations(id), "Missing membership response")));
    }
}
