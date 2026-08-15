package com.fooddelivery.restaurant.entity;

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
public class RestaurantOrder {
    @java.lang.SuppressWarnings("all")

    @Id
    @Column(name = "order_id")
    private UUID orderId;
    @Column(name = "restaurant_id")
    private UUID restaurantId;
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "status")
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
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "rider_name")
    private String riderName;
    @Column(name = "items_json", columnDefinition = "TEXT")
    private String itemsJson;
    @Column(name = "created_at")
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


    @java.lang.SuppressWarnings("all")
    public static class RestaurantOrderBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID orderId;
        @java.lang.SuppressWarnings("all")
        private UUID restaurantId;
        @java.lang.SuppressWarnings("all")
        private OrderStatus status;
        @java.lang.SuppressWarnings("all")
        private com.fooddelivery.common.enums.DeliveryStatus deliveryStatus;
        @java.lang.SuppressWarnings("all")
        private PaymentIntentStatus paymentStatus;
        @java.lang.SuppressWarnings("all")
        private Integer version;
        @java.lang.SuppressWarnings("all")
        private Integer prepTime;
        @java.lang.SuppressWarnings("all")
        private Integer additionalPrepTime;
        @java.lang.SuppressWarnings("all")
        private Long estimatedCompletionTime;
        @java.lang.SuppressWarnings("all")
        private Double deliveryLat;
        @java.lang.SuppressWarnings("all")
        private Double deliveryLng;
        @java.lang.SuppressWarnings("all")
        private String deliveryAddress;
        @java.lang.SuppressWarnings("all")
        private String pickupOtp;
        @java.lang.SuppressWarnings("all")
        private String deliveryOtp;
        @java.lang.SuppressWarnings("all")
        private UUID deliveryExecutiveId;
        @java.lang.SuppressWarnings("all")
        private String customerName;
        @java.lang.SuppressWarnings("all")
        private String riderName;
        @java.lang.SuppressWarnings("all")
        private String itemsJson;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime createdAt;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime updatedAt;

        @java.lang.SuppressWarnings("all")
        RestaurantOrderBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder orderId(final UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder restaurantId(final UUID restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder status(final OrderStatus status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder deliveryStatus(final com.fooddelivery.common.enums.DeliveryStatus deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder paymentStatus(final PaymentIntentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder prepTime(final Integer prepTime) {
            this.prepTime = prepTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder additionalPrepTime(final Integer additionalPrepTime) {
            this.additionalPrepTime = additionalPrepTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder estimatedCompletionTime(final Long estimatedCompletionTime) {
            this.estimatedCompletionTime = estimatedCompletionTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder deliveryLat(final Double deliveryLat) {
            this.deliveryLat = deliveryLat;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder deliveryLng(final Double deliveryLng) {
            this.deliveryLng = deliveryLng;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder deliveryAddress(final String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder pickupOtp(final String pickupOtp) {
            this.pickupOtp = pickupOtp;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder deliveryOtp(final String deliveryOtp) {
            this.deliveryOtp = deliveryOtp;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder deliveryExecutiveId(final UUID deliveryExecutiveId) {
            this.deliveryExecutiveId = deliveryExecutiveId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder customerName(final String customerName) {
            this.customerName = customerName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder riderName(final String riderName) {
            this.riderName = riderName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder itemsJson(final String itemsJson) {
            this.itemsJson = itemsJson;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrder.RestaurantOrderBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public RestaurantOrder build() {
            return new RestaurantOrder(this.orderId, this.restaurantId, this.status, this.deliveryStatus, this.paymentStatus, this.version, this.prepTime, this.additionalPrepTime, this.estimatedCompletionTime, this.deliveryLat, this.deliveryLng, this.deliveryAddress, this.pickupOtp, this.deliveryOtp, this.deliveryExecutiveId, this.customerName, this.riderName, this.itemsJson, this.createdAt, this.updatedAt);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOrder.RestaurantOrderBuilder(orderId=" + this.orderId + ", restaurantId=" + this.restaurantId + ", status=" + this.status + ", deliveryStatus=" + this.deliveryStatus + ", paymentStatus=" + this.paymentStatus + ", version=" + this.version + ", prepTime=" + this.prepTime + ", additionalPrepTime=" + this.additionalPrepTime + ", estimatedCompletionTime=" + this.estimatedCompletionTime + ", deliveryLat=" + this.deliveryLat + ", deliveryLng=" + this.deliveryLng + ", deliveryAddress=" + this.deliveryAddress + ", pickupOtp=" + this.pickupOtp + ", deliveryOtp=" + this.deliveryOtp + ", deliveryExecutiveId=" + this.deliveryExecutiveId + ", customerName=" + this.customerName + ", riderName=" + this.riderName + ", itemsJson=" + this.itemsJson + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static RestaurantOrder.RestaurantOrderBuilder builder() {
        return new RestaurantOrder.RestaurantOrderBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getOrderId() {
        return this.orderId;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getRestaurantId() {
        return this.restaurantId;
    }

    @java.lang.SuppressWarnings("all")
    public OrderStatus getStatus() {
        return this.status;
    }

    @java.lang.SuppressWarnings("all")
    public com.fooddelivery.common.enums.DeliveryStatus getDeliveryStatus() {
        return this.deliveryStatus;
    }

    @java.lang.SuppressWarnings("all")
    public PaymentIntentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getVersion() {
        return this.version;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getPrepTime() {
        return this.prepTime;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getAdditionalPrepTime() {
        return this.additionalPrepTime;
    }

    @java.lang.SuppressWarnings("all")
    public Long getEstimatedCompletionTime() {
        return this.estimatedCompletionTime;
    }

    @java.lang.SuppressWarnings("all")
    public Double getDeliveryLat() {
        return this.deliveryLat;
    }

    @java.lang.SuppressWarnings("all")
    public Double getDeliveryLng() {
        return this.deliveryLng;
    }

    @java.lang.SuppressWarnings("all")
    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    @java.lang.SuppressWarnings("all")
    public String getPickupOtp() {
        return this.pickupOtp;
    }

    @java.lang.SuppressWarnings("all")
    public String getDeliveryOtp() {
        return this.deliveryOtp;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getDeliveryExecutiveId() {
        return this.deliveryExecutiveId;
    }

    @java.lang.SuppressWarnings("all")
    public String getCustomerName() {
        return this.customerName;
    }

    @java.lang.SuppressWarnings("all")
    public String getRiderName() {
        return this.riderName;
    }

    @java.lang.SuppressWarnings("all")
    public String getItemsJson() {
        return this.itemsJson;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setOrderId(final UUID orderId) {
        this.orderId = orderId;
    }

    @java.lang.SuppressWarnings("all")
    public void setRestaurantId(final UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeliveryStatus(final com.fooddelivery.common.enums.DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @java.lang.SuppressWarnings("all")
    public void setPaymentStatus(final PaymentIntentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Integer version) {
        this.version = version;
    }

    @java.lang.SuppressWarnings("all")
    public void setPrepTime(final Integer prepTime) {
        this.prepTime = prepTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setAdditionalPrepTime(final Integer additionalPrepTime) {
        this.additionalPrepTime = additionalPrepTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setEstimatedCompletionTime(final Long estimatedCompletionTime) {
        this.estimatedCompletionTime = estimatedCompletionTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeliveryLat(final Double deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeliveryLng(final Double deliveryLng) {
        this.deliveryLng = deliveryLng;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeliveryAddress(final String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @java.lang.SuppressWarnings("all")
    public void setPickupOtp(final String pickupOtp) {
        this.pickupOtp = pickupOtp;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeliveryOtp(final String deliveryOtp) {
        this.deliveryOtp = deliveryOtp;
    }

    @java.lang.SuppressWarnings("all")
    public void setDeliveryExecutiveId(final UUID deliveryExecutiveId) {
        this.deliveryExecutiveId = deliveryExecutiveId;
    }

    @java.lang.SuppressWarnings("all")
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    @java.lang.SuppressWarnings("all")
    public void setRiderName(final String riderName) {
        this.riderName = riderName;
    }

    @java.lang.SuppressWarnings("all")
    public void setItemsJson(final String itemsJson) {
        this.itemsJson = itemsJson;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof RestaurantOrder)) return false;
        final RestaurantOrder other = (RestaurantOrder) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$prepTime = this.getPrepTime();
        final java.lang.Object other$prepTime = other.getPrepTime();
        if (this$prepTime == null ? other$prepTime != null : !this$prepTime.equals(other$prepTime)) return false;
        final java.lang.Object this$additionalPrepTime = this.getAdditionalPrepTime();
        final java.lang.Object other$additionalPrepTime = other.getAdditionalPrepTime();
        if (this$additionalPrepTime == null ? other$additionalPrepTime != null : !this$additionalPrepTime.equals(other$additionalPrepTime)) return false;
        final java.lang.Object this$estimatedCompletionTime = this.getEstimatedCompletionTime();
        final java.lang.Object other$estimatedCompletionTime = other.getEstimatedCompletionTime();
        if (this$estimatedCompletionTime == null ? other$estimatedCompletionTime != null : !this$estimatedCompletionTime.equals(other$estimatedCompletionTime)) return false;
        final java.lang.Object this$deliveryLat = this.getDeliveryLat();
        final java.lang.Object other$deliveryLat = other.getDeliveryLat();
        if (this$deliveryLat == null ? other$deliveryLat != null : !this$deliveryLat.equals(other$deliveryLat)) return false;
        final java.lang.Object this$deliveryLng = this.getDeliveryLng();
        final java.lang.Object other$deliveryLng = other.getDeliveryLng();
        if (this$deliveryLng == null ? other$deliveryLng != null : !this$deliveryLng.equals(other$deliveryLng)) return false;
        final java.lang.Object this$orderId = this.getOrderId();
        final java.lang.Object other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
        final java.lang.Object this$restaurantId = this.getRestaurantId();
        final java.lang.Object other$restaurantId = other.getRestaurantId();
        if (this$restaurantId == null ? other$restaurantId != null : !this$restaurantId.equals(other$restaurantId)) return false;
        final java.lang.Object this$status = this.getStatus();
        final java.lang.Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final java.lang.Object this$deliveryStatus = this.getDeliveryStatus();
        final java.lang.Object other$deliveryStatus = other.getDeliveryStatus();
        if (this$deliveryStatus == null ? other$deliveryStatus != null : !this$deliveryStatus.equals(other$deliveryStatus)) return false;
        final java.lang.Object this$paymentStatus = this.getPaymentStatus();
        final java.lang.Object other$paymentStatus = other.getPaymentStatus();
        if (this$paymentStatus == null ? other$paymentStatus != null : !this$paymentStatus.equals(other$paymentStatus)) return false;
        final java.lang.Object this$deliveryAddress = this.getDeliveryAddress();
        final java.lang.Object other$deliveryAddress = other.getDeliveryAddress();
        if (this$deliveryAddress == null ? other$deliveryAddress != null : !this$deliveryAddress.equals(other$deliveryAddress)) return false;
        final java.lang.Object this$pickupOtp = this.getPickupOtp();
        final java.lang.Object other$pickupOtp = other.getPickupOtp();
        if (this$pickupOtp == null ? other$pickupOtp != null : !this$pickupOtp.equals(other$pickupOtp)) return false;
        final java.lang.Object this$deliveryOtp = this.getDeliveryOtp();
        final java.lang.Object other$deliveryOtp = other.getDeliveryOtp();
        if (this$deliveryOtp == null ? other$deliveryOtp != null : !this$deliveryOtp.equals(other$deliveryOtp)) return false;
        final java.lang.Object this$deliveryExecutiveId = this.getDeliveryExecutiveId();
        final java.lang.Object other$deliveryExecutiveId = other.getDeliveryExecutiveId();
        if (this$deliveryExecutiveId == null ? other$deliveryExecutiveId != null : !this$deliveryExecutiveId.equals(other$deliveryExecutiveId)) return false;
        final java.lang.Object this$customerName = this.getCustomerName();
        final java.lang.Object other$customerName = other.getCustomerName();
        if (this$customerName == null ? other$customerName != null : !this$customerName.equals(other$customerName)) return false;
        final java.lang.Object this$riderName = this.getRiderName();
        final java.lang.Object other$riderName = other.getRiderName();
        if (this$riderName == null ? other$riderName != null : !this$riderName.equals(other$riderName)) return false;
        final java.lang.Object this$itemsJson = this.getItemsJson();
        final java.lang.Object other$itemsJson = other.getItemsJson();
        if (this$itemsJson == null ? other$itemsJson != null : !this$itemsJson.equals(other$itemsJson)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$updatedAt = this.getUpdatedAt();
        final java.lang.Object other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof RestaurantOrder;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $prepTime = this.getPrepTime();
        result = result * PRIME + ($prepTime == null ? 43 : $prepTime.hashCode());
        final java.lang.Object $additionalPrepTime = this.getAdditionalPrepTime();
        result = result * PRIME + ($additionalPrepTime == null ? 43 : $additionalPrepTime.hashCode());
        final java.lang.Object $estimatedCompletionTime = this.getEstimatedCompletionTime();
        result = result * PRIME + ($estimatedCompletionTime == null ? 43 : $estimatedCompletionTime.hashCode());
        final java.lang.Object $deliveryLat = this.getDeliveryLat();
        result = result * PRIME + ($deliveryLat == null ? 43 : $deliveryLat.hashCode());
        final java.lang.Object $deliveryLng = this.getDeliveryLng();
        result = result * PRIME + ($deliveryLng == null ? 43 : $deliveryLng.hashCode());
        final java.lang.Object $orderId = this.getOrderId();
        result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
        final java.lang.Object $restaurantId = this.getRestaurantId();
        result = result * PRIME + ($restaurantId == null ? 43 : $restaurantId.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final java.lang.Object $deliveryStatus = this.getDeliveryStatus();
        result = result * PRIME + ($deliveryStatus == null ? 43 : $deliveryStatus.hashCode());
        final java.lang.Object $paymentStatus = this.getPaymentStatus();
        result = result * PRIME + ($paymentStatus == null ? 43 : $paymentStatus.hashCode());
        final java.lang.Object $deliveryAddress = this.getDeliveryAddress();
        result = result * PRIME + ($deliveryAddress == null ? 43 : $deliveryAddress.hashCode());
        final java.lang.Object $pickupOtp = this.getPickupOtp();
        result = result * PRIME + ($pickupOtp == null ? 43 : $pickupOtp.hashCode());
        final java.lang.Object $deliveryOtp = this.getDeliveryOtp();
        result = result * PRIME + ($deliveryOtp == null ? 43 : $deliveryOtp.hashCode());
        final java.lang.Object $deliveryExecutiveId = this.getDeliveryExecutiveId();
        result = result * PRIME + ($deliveryExecutiveId == null ? 43 : $deliveryExecutiveId.hashCode());
        final java.lang.Object $customerName = this.getCustomerName();
        result = result * PRIME + ($customerName == null ? 43 : $customerName.hashCode());
        final java.lang.Object $riderName = this.getRiderName();
        result = result * PRIME + ($riderName == null ? 43 : $riderName.hashCode());
        final java.lang.Object $itemsJson = this.getItemsJson();
        result = result * PRIME + ($itemsJson == null ? 43 : $itemsJson.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "RestaurantOrder(orderId=" + this.getOrderId() + ", restaurantId=" + this.getRestaurantId() + ", status=" + this.getStatus() + ", deliveryStatus=" + this.getDeliveryStatus() + ", paymentStatus=" + this.getPaymentStatus() + ", version=" + this.getVersion() + ", prepTime=" + this.getPrepTime() + ", additionalPrepTime=" + this.getAdditionalPrepTime() + ", estimatedCompletionTime=" + this.getEstimatedCompletionTime() + ", deliveryLat=" + this.getDeliveryLat() + ", deliveryLng=" + this.getDeliveryLng() + ", deliveryAddress=" + this.getDeliveryAddress() + ", pickupOtp=" + this.getPickupOtp() + ", deliveryOtp=" + this.getDeliveryOtp() + ", deliveryExecutiveId=" + this.getDeliveryExecutiveId() + ", customerName=" + this.getCustomerName() + ", riderName=" + this.getRiderName() + ", itemsJson=" + this.getItemsJson() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public RestaurantOrder() {
    }

    @java.lang.SuppressWarnings("all")
    public RestaurantOrder(final UUID orderId, final UUID restaurantId, final OrderStatus status, final com.fooddelivery.common.enums.DeliveryStatus deliveryStatus, final PaymentIntentStatus paymentStatus, final Integer version, final Integer prepTime, final Integer additionalPrepTime, final Long estimatedCompletionTime, final Double deliveryLat, final Double deliveryLng, final String deliveryAddress, final String pickupOtp, final String deliveryOtp, final UUID deliveryExecutiveId, final String customerName, final String riderName, final String itemsJson, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.status = status;
        this.deliveryStatus = deliveryStatus;
        this.paymentStatus = paymentStatus;
        this.version = version;
        this.prepTime = prepTime;
        this.additionalPrepTime = additionalPrepTime;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
        this.deliveryAddress = deliveryAddress;
        this.pickupOtp = pickupOtp;
        this.deliveryOtp = deliveryOtp;
        this.deliveryExecutiveId = deliveryExecutiveId;
        this.customerName = customerName;
        this.riderName = riderName;
        this.itemsJson = itemsJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
