package com.fooddelivery.restaurant.dto;

import java.util.List;
import java.util.UUID;

public class SetOutletCategoryTimingRequest {
    private UUID categoryId;
    private List<TimingDTO> timings;


public static class SetOutletCategoryTimingRequestBuilder {
private UUID categoryId;
private List<TimingDTO> timings;

SetOutletCategoryTimingRequestBuilder() {
        }

        /**
         * @return {@code this}.
         */
public SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder categoryId(final UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder timings(final List<TimingDTO> timings) {
            this.timings = timings;
            return this;
        }

public SetOutletCategoryTimingRequest build() {
            return new SetOutletCategoryTimingRequest(this.categoryId, this.timings);
        }

        @java.lang.Override
public java.lang.String toString() {
            return "SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder(categoryId=" + this.categoryId + ", timings=" + this.timings + ")";
        }
    }

public static SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder builder() {
        return new SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder();
    }

public UUID getCategoryId() {
        return this.categoryId;
    }

public List<TimingDTO> getTimings() {
        return this.timings;
    }

public void setCategoryId(final UUID categoryId) {
        this.categoryId = categoryId;
    }

public void setTimings(final List<TimingDTO> timings) {
        this.timings = timings;
    }

    @java.lang.Override
public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SetOutletCategoryTimingRequest)) return false;
        final SetOutletCategoryTimingRequest other = (SetOutletCategoryTimingRequest) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$categoryId = this.getCategoryId();
        final java.lang.Object other$categoryId = other.getCategoryId();
        if (this$categoryId == null ? other$categoryId != null : !this$categoryId.equals(other$categoryId)) return false;
        final java.lang.Object this$timings = this.getTimings();
        final java.lang.Object other$timings = other.getTimings();
        if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
        return true;
    }

protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SetOutletCategoryTimingRequest;
    }

    @java.lang.Override
public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $categoryId = this.getCategoryId();
        result = result * PRIME + ($categoryId == null ? 43 : $categoryId.hashCode());
        final java.lang.Object $timings = this.getTimings();
        result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
        return result;
    }

    @java.lang.Override
public java.lang.String toString() {
        return "SetOutletCategoryTimingRequest(categoryId=" + this.getCategoryId() + ", timings=" + this.getTimings() + ")";
    }

public SetOutletCategoryTimingRequest() {
    }

public SetOutletCategoryTimingRequest(final UUID categoryId, final List<TimingDTO> timings) {
        this.categoryId = categoryId;
        this.timings = timings;
    }
}
