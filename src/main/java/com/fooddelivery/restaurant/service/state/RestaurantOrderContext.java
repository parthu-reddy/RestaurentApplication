package com.fooddelivery.restaurant.service.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import java.util.UUID;

public class RestaurantOrderContext {
    private RestaurantOrder order;
    private JsonNode eventPayload;
    private RestaurantActionService actionService;
    private UUID restaurantId;
    // Additional parameters for API requests
    private Integer additionalPrepTime;
    private String delayReason;
    private String cancelReason;
    private String rejectReason;
    // Additional contextual data
    private double restaurantLat;
    private double restaurantLng;

    @java.lang.SuppressWarnings("all")
    RestaurantOrderContext(final RestaurantOrder order, final JsonNode eventPayload, final RestaurantActionService actionService, final UUID restaurantId, final Integer additionalPrepTime, final String delayReason, final String cancelReason, final String rejectReason, final double restaurantLat, final double restaurantLng) {
        this.order = order;
        this.eventPayload = eventPayload;
        this.actionService = actionService;
        this.restaurantId = restaurantId;
        this.additionalPrepTime = additionalPrepTime;
        this.delayReason = delayReason;
        this.cancelReason = cancelReason;
        this.rejectReason = rejectReason;
        this.restaurantLat = restaurantLat;
        this.restaurantLng = restaurantLng;
    }


    @java.lang.SuppressWarnings("all")
    public static class RestaurantOrderContextBuilder {
        @java.lang.SuppressWarnings("all")
        private RestaurantOrder order;
        @java.lang.SuppressWarnings("all")
        private JsonNode eventPayload;
        @java.lang.SuppressWarnings("all")
        private RestaurantActionService actionService;
        @java.lang.SuppressWarnings("all")
        private UUID restaurantId;
        @java.lang.SuppressWarnings("all")
        private Integer additionalPrepTime;
        @java.lang.SuppressWarnings("all")
        private String delayReason;
        @java.lang.SuppressWarnings("all")
        private String cancelReason;
        @java.lang.SuppressWarnings("all")
        private String rejectReason;
        @java.lang.SuppressWarnings("all")
        private double restaurantLat;
        @java.lang.SuppressWarnings("all")
        private double restaurantLng;

        @java.lang.SuppressWarnings("all")
        RestaurantOrderContextBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder order(final RestaurantOrder order) {
            this.order = order;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder eventPayload(final JsonNode eventPayload) {
            this.eventPayload = eventPayload;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder actionService(final RestaurantActionService actionService) {
            this.actionService = actionService;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder restaurantId(final UUID restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder additionalPrepTime(final Integer additionalPrepTime) {
            this.additionalPrepTime = additionalPrepTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder delayReason(final String delayReason) {
            this.delayReason = delayReason;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder cancelReason(final String cancelReason) {
            this.cancelReason = cancelReason;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder rejectReason(final String rejectReason) {
            this.rejectReason = rejectReason;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder restaurantLat(final double restaurantLat) {
            this.restaurantLat = restaurantLat;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext.RestaurantOrderContextBuilder restaurantLng(final double restaurantLng) {
            this.restaurantLng = restaurantLng;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public RestaurantOrderContext build() {
            return new RestaurantOrderContext(this.order, this.eventPayload, this.actionService, this.restaurantId, this.additionalPrepTime, this.delayReason, this.cancelReason, this.rejectReason, this.restaurantLat, this.restaurantLng);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOrderContext.RestaurantOrderContextBuilder(order=" + this.order + ", eventPayload=" + this.eventPayload + ", actionService=" + this.actionService + ", restaurantId=" + this.restaurantId + ", additionalPrepTime=" + this.additionalPrepTime + ", delayReason=" + this.delayReason + ", cancelReason=" + this.cancelReason + ", rejectReason=" + this.rejectReason + ", restaurantLat=" + this.restaurantLat + ", restaurantLng=" + this.restaurantLng + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static RestaurantOrderContext.RestaurantOrderContextBuilder builder() {
        return new RestaurantOrderContext.RestaurantOrderContextBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public RestaurantOrder getOrder() {
        return this.order;
    }

    @java.lang.SuppressWarnings("all")
    public JsonNode getEventPayload() {
        return this.eventPayload;
    }

    @java.lang.SuppressWarnings("all")
    public RestaurantActionService getActionService() {
        return this.actionService;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getRestaurantId() {
        return this.restaurantId;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getAdditionalPrepTime() {
        return this.additionalPrepTime;
    }

    @java.lang.SuppressWarnings("all")
    public String getDelayReason() {
        return this.delayReason;
    }

    @java.lang.SuppressWarnings("all")
    public String getCancelReason() {
        return this.cancelReason;
    }

    @java.lang.SuppressWarnings("all")
    public String getRejectReason() {
        return this.rejectReason;
    }

    @java.lang.SuppressWarnings("all")
    public double getRestaurantLat() {
        return this.restaurantLat;
    }

    @java.lang.SuppressWarnings("all")
    public double getRestaurantLng() {
        return this.restaurantLng;
    }

    @java.lang.SuppressWarnings("all")
    public void setOrder(final RestaurantOrder order) {
        this.order = order;
    }

    @java.lang.SuppressWarnings("all")
    public void setEventPayload(final JsonNode eventPayload) {
        this.eventPayload = eventPayload;
    }

    @java.lang.SuppressWarnings("all")
    public void setActionService(final RestaurantActionService actionService) {
        this.actionService = actionService;
    }

    @java.lang.SuppressWarnings("all")
    public void setRestaurantId(final UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    @java.lang.SuppressWarnings("all")
    public void setAdditionalPrepTime(final Integer additionalPrepTime) {
        this.additionalPrepTime = additionalPrepTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setDelayReason(final String delayReason) {
        this.delayReason = delayReason;
    }

    @java.lang.SuppressWarnings("all")
    public void setCancelReason(final String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @java.lang.SuppressWarnings("all")
    public void setRejectReason(final String rejectReason) {
        this.rejectReason = rejectReason;
    }

    @java.lang.SuppressWarnings("all")
    public void setRestaurantLat(final double restaurantLat) {
        this.restaurantLat = restaurantLat;
    }

    @java.lang.SuppressWarnings("all")
    public void setRestaurantLng(final double restaurantLng) {
        this.restaurantLng = restaurantLng;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof RestaurantOrderContext)) return false;
        final RestaurantOrderContext other = (RestaurantOrderContext) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (java.lang.Double.compare(this.getRestaurantLat(), other.getRestaurantLat()) != 0) return false;
        if (java.lang.Double.compare(this.getRestaurantLng(), other.getRestaurantLng()) != 0) return false;
        final java.lang.Object this$additionalPrepTime = this.getAdditionalPrepTime();
        final java.lang.Object other$additionalPrepTime = other.getAdditionalPrepTime();
        if (this$additionalPrepTime == null ? other$additionalPrepTime != null : !this$additionalPrepTime.equals(other$additionalPrepTime)) return false;
        final java.lang.Object this$order = this.getOrder();
        final java.lang.Object other$order = other.getOrder();
        if (this$order == null ? other$order != null : !this$order.equals(other$order)) return false;
        final java.lang.Object this$eventPayload = this.getEventPayload();
        final java.lang.Object other$eventPayload = other.getEventPayload();
        if (this$eventPayload == null ? other$eventPayload != null : !this$eventPayload.equals(other$eventPayload)) return false;
        final java.lang.Object this$actionService = this.getActionService();
        final java.lang.Object other$actionService = other.getActionService();
        if (this$actionService == null ? other$actionService != null : !this$actionService.equals(other$actionService)) return false;
        final java.lang.Object this$restaurantId = this.getRestaurantId();
        final java.lang.Object other$restaurantId = other.getRestaurantId();
        if (this$restaurantId == null ? other$restaurantId != null : !this$restaurantId.equals(other$restaurantId)) return false;
        final java.lang.Object this$delayReason = this.getDelayReason();
        final java.lang.Object other$delayReason = other.getDelayReason();
        if (this$delayReason == null ? other$delayReason != null : !this$delayReason.equals(other$delayReason)) return false;
        final java.lang.Object this$cancelReason = this.getCancelReason();
        final java.lang.Object other$cancelReason = other.getCancelReason();
        if (this$cancelReason == null ? other$cancelReason != null : !this$cancelReason.equals(other$cancelReason)) return false;
        final java.lang.Object this$rejectReason = this.getRejectReason();
        final java.lang.Object other$rejectReason = other.getRejectReason();
        if (this$rejectReason == null ? other$rejectReason != null : !this$rejectReason.equals(other$rejectReason)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof RestaurantOrderContext;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $restaurantLat = java.lang.Double.doubleToLongBits(this.getRestaurantLat());
        result = result * PRIME + (int) ($restaurantLat >>> 32 ^ $restaurantLat);
        final long $restaurantLng = java.lang.Double.doubleToLongBits(this.getRestaurantLng());
        result = result * PRIME + (int) ($restaurantLng >>> 32 ^ $restaurantLng);
        final java.lang.Object $additionalPrepTime = this.getAdditionalPrepTime();
        result = result * PRIME + ($additionalPrepTime == null ? 43 : $additionalPrepTime.hashCode());
        final java.lang.Object $order = this.getOrder();
        result = result * PRIME + ($order == null ? 43 : $order.hashCode());
        final java.lang.Object $eventPayload = this.getEventPayload();
        result = result * PRIME + ($eventPayload == null ? 43 : $eventPayload.hashCode());
        final java.lang.Object $actionService = this.getActionService();
        result = result * PRIME + ($actionService == null ? 43 : $actionService.hashCode());
        final java.lang.Object $restaurantId = this.getRestaurantId();
        result = result * PRIME + ($restaurantId == null ? 43 : $restaurantId.hashCode());
        final java.lang.Object $delayReason = this.getDelayReason();
        result = result * PRIME + ($delayReason == null ? 43 : $delayReason.hashCode());
        final java.lang.Object $cancelReason = this.getCancelReason();
        result = result * PRIME + ($cancelReason == null ? 43 : $cancelReason.hashCode());
        final java.lang.Object $rejectReason = this.getRejectReason();
        result = result * PRIME + ($rejectReason == null ? 43 : $rejectReason.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "RestaurantOrderContext(order=" + this.getOrder() + ", eventPayload=" + this.getEventPayload() + ", actionService=" + this.getActionService() + ", restaurantId=" + this.getRestaurantId() + ", additionalPrepTime=" + this.getAdditionalPrepTime() + ", delayReason=" + this.getDelayReason() + ", cancelReason=" + this.getCancelReason() + ", rejectReason=" + this.getRejectReason() + ", restaurantLat=" + this.getRestaurantLat() + ", restaurantLng=" + this.getRestaurantLng() + ")";
    }
}
