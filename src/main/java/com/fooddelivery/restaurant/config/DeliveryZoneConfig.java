package com.fooddelivery.restaurant.config;

import com.fooddelivery.common.constants.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Compatibility defaults for orders produced before dispatch scope became part of ORDER_PAID.
 * New producers send and persist the scope on every order; these values are used only when
 * consuming an older event during a rolling deployment or replaying retained Kafka records.
 */
@Configuration
@ConfigurationProperties(prefix = "delivery.zone")
@Validated
@lombok.Data
public class DeliveryZoneConfig {
    @NotBlank
    private String defaultCity = "BLR";

    @Positive
    private double fleetSearchRadiusKm = AppConstants.FLEET_SEARCH_RADIUS_KM;
}
