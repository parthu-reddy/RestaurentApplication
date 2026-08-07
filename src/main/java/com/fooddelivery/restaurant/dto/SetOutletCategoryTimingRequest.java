package com.fooddelivery.restaurant.dto;

import java.util.List;
import java.util.UUID;

public class SetOutletCategoryTimingRequest {
    private UUID categoryId;
    private List<TimingDTO> timings;


    @java.lang.SuppressWarnings("all")
    public static class SetOutletCategoryTimingRequestBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID categoryId;
        @java.lang.SuppressWarnings("all")
        private List<TimingDTO> timings;

        @java.lang.SuppressWarnings("all")
        SetOutletCategoryTimingRequestBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder categoryId(final UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder timings(final List<TimingDTO> timings) {
            this.timings = timings;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public SetOutletCategoryTimingRequest build() {
            return new SetOutletCategoryTimingRequest(this.categoryId, this.timings);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder(categoryId=" + this.categoryId + ", timings=" + this.timings + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder builder() {
        return new SetOutletCategoryTimingRequest.SetOutletCategoryTimingRequestBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getCategoryId() {
        return this.categoryId;
    }

    @java.lang.SuppressWarnings("all")
    public List<TimingDTO> getTimings() {
        return this.timings;
    }

    @java.lang.SuppressWarnings("all")
    public void setCategoryId(final UUID categoryId) {
        this.categoryId = categoryId;
    }

    @java.lang.SuppressWarnings("all")
    public void setTimings(final List<TimingDTO> timings) {
        this.timings = timings;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
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

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SetOutletCategoryTimingRequest;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
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
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SetOutletCategoryTimingRequest(categoryId=" + this.getCategoryId() + ", timings=" + this.getTimings() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public SetOutletCategoryTimingRequest() {
    }

    @java.lang.SuppressWarnings("all")
    public SetOutletCategoryTimingRequest(final UUID categoryId, final List<TimingDTO> timings) {
        this.categoryId = categoryId;
        this.timings = timings;
    }
}
