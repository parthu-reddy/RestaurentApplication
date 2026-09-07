package com.fooddelivery.restaurant.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.fooddelivery.common.constants.PaymentIntentStatus;

@Entity
@Table(name = "restaurant_orders", indexes = {@jakarta.persistence.Index(name = "idx_rest_order_restaurant", columnList = "restaurantId"), @jakarta.persistence.Index(name = "idx_rest_order_status", columnList = "status")})
@lombok.extern.slf4j.Slf4j
@lombok.Getter
@lombok.Setter
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class RestaurantOrder {
@Id
    @Column(name = "order_id")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotNull
    private UUID orderId;
    @Column(name = "restaurant_id")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private UUID restaurantId;
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "status")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private OrderStatus status;

    public void setStatus(OrderStatus status) {
        if (this.status != null && status != null) {
            if (status.getSequence() < this.status.getSequence()) {
                log.error("Invalid state transition: Attempted to move restaurant order {} backward from {} to {}", this.orderId, this.status, status);
                throw new IllegalStateException("Cannot move order status backward from " + this.status + " to " + status);
            }
        }
        if (this.status != status) {
            log.info("Restaurant order {} status changing from {} to {}", this.orderId, this.status, status);
        }
        this.status = status;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private com.fooddelivery.common.enums.DeliveryStatus deliveryStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentIntentStatus paymentStatus;
    @Version
    @Column(name = "version")
    private Integer version;
    @Column(name = "prep_time")
    private Integer prepTime;
    @Column(name = "additional_prep_time")
    private Integer additionalPrepTime;
    @Column(name = "estimated_completion_time")
    private Long estimatedCompletionTime;
    @Column(name = "delivery_lat")
    private Double deliveryLat;
    @Column(name = "delivery_lng")
    private Double deliveryLng;
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Column(name = "pickup_otp")
    private String pickupOtp;
    @Column(name = "delivery_otp")
    private String deliveryOtp;
    @Column(name = "delivery_executive_id")
    private UUID deliveryExecutiveId;
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "rider_name")
    @com.fasterxml.jackson.annotation.JsonProperty("deliveryExecutiveName")
    private String riderName;
    @Column(name = "items_json", columnDefinition = "TEXT")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String itemsJson;
    
    @com.fasterxml.jackson.annotation.JsonProperty("items")
    public Object getItems() {
        if (itemsJson == null || itemsJson.isEmpty() || "[]".equals(itemsJson)) {
            return java.util.Collections.emptyList();
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(itemsJson, Object.class);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    @Column(name = "total_amount")
    @com.fasterxml.jackson.annotation.JsonProperty("total")
    private java.math.BigDecimal totalAmount;
    @Column(name = "food_cost")
    private java.math.BigDecimal foodCost;
    @Column(name = "restaurant_platform_fee")
    private java.math.BigDecimal restaurantPlatformFee;
    @Column(name = "restaurant_delivery_contribution")
    private java.math.BigDecimal restaurantDeliveryContribution;
    @Column(name = "platform_bonus")
    private java.math.BigDecimal platformBonus;
    @Column(name = "restaurant_payout")
    private java.math.BigDecimal restaurantPayout;
    @Column(name = "created_at")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }















































}
