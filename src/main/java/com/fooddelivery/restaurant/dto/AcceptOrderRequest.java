package com.fooddelivery.restaurant.dto;

public class AcceptOrderRequest {
    private Integer additionalPrepTime;
    private String delayReason;

public AcceptOrderRequest() {
    }

public Integer getAdditionalPrepTime() {
        return this.additionalPrepTime;
    }

public String getDelayReason() {
        return this.delayReason;
    }

public void setAdditionalPrepTime(final Integer additionalPrepTime) {
        this.additionalPrepTime = additionalPrepTime;
    }

public void setDelayReason(final String delayReason) {
        this.delayReason = delayReason;
    }

    @java.lang.Override
public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AcceptOrderRequest)) return false;
        final AcceptOrderRequest other = (AcceptOrderRequest) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$additionalPrepTime = this.getAdditionalPrepTime();
        final java.lang.Object other$additionalPrepTime = other.getAdditionalPrepTime();
        if (this$additionalPrepTime == null ? other$additionalPrepTime != null : !this$additionalPrepTime.equals(other$additionalPrepTime)) return false;
        final java.lang.Object this$delayReason = this.getDelayReason();
        final java.lang.Object other$delayReason = other.getDelayReason();
        if (this$delayReason == null ? other$delayReason != null : !this$delayReason.equals(other$delayReason)) return false;
        return true;
    }

protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AcceptOrderRequest;
    }

    @java.lang.Override
public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $additionalPrepTime = this.getAdditionalPrepTime();
        result = result * PRIME + ($additionalPrepTime == null ? 43 : $additionalPrepTime.hashCode());
        final java.lang.Object $delayReason = this.getDelayReason();
        result = result * PRIME + ($delayReason == null ? 43 : $delayReason.hashCode());
        return result;
    }

    @java.lang.Override
public java.lang.String toString() {
        return "AcceptOrderRequest(additionalPrepTime=" + this.getAdditionalPrepTime() + ", delayReason=" + this.getDelayReason() + ")";
    }
}
