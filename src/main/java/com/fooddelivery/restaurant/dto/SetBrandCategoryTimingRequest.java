package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class SetBrandCategoryTimingRequest {
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    private List<TimingDTO> timings;


public static class SetBrandCategoryTimingRequestBuilder {
private UUID categoryId;
private List<TimingDTO> timings;

SetBrandCategoryTimingRequestBuilder() {
        }

        /**
         * @return {@code this}.
         */
public SetBrandCategoryTimingRequest.SetBrandCategoryTimingRequestBuilder categoryId(final UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public SetBrandCategoryTimingRequest.SetBrandCategoryTimingRequestBuilder timings(final List<TimingDTO> timings) {
            this.timings = timings;
            return this;
        }

public SetBrandCategoryTimingRequest build() {
            return new SetBrandCategoryTimingRequest(this.categoryId, this.timings);
        }

        @java.lang.Override
public java.lang.String toString() {
            return "SetBrandCategoryTimingRequest.SetBrandCategoryTimingRequestBuilder(categoryId=" + this.categoryId + ", timings=" + this.timings + ")";
        }
    }

public static SetBrandCategoryTimingRequest.SetBrandCategoryTimingRequestBuilder builder() {
        return new SetBrandCategoryTimingRequest.SetBrandCategoryTimingRequestBuilder();
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
        if (!(o instanceof SetBrandCategoryTimingRequest)) return false;
        final SetBrandCategoryTimingRequest other = (SetBrandCategoryTimingRequest) o;
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
        return other instanceof SetBrandCategoryTimingRequest;
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
        return "SetBrandCategoryTimingRequest(categoryId=" + this.getCategoryId() + ", timings=" + this.getTimings() + ")";
    }

public SetBrandCategoryTimingRequest() {
    }

public SetBrandCategoryTimingRequest(final UUID categoryId, final List<TimingDTO> timings) {
        this.categoryId = categoryId;
        this.timings = timings;
    }
}
