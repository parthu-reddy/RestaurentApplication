package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class OutletOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String fssaiLicenseNumber;
        @NotNull
        private Double lat;
        @NotNull
        private Double lng;
        @NotNull
        private List<TimingRequest> timings;
        private String bannerUrl;
        private String cuisine;
        private Double rating;
        private Integer reviewsCount;
        private Integer deliveryTime;
        private Double deliveryFee;
        private String tags;

        @java.lang.SuppressWarnings("all")
        public OutletOnboardRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public String getName() {
            return this.name;
        }

        @java.lang.SuppressWarnings("all")
        public String getFssaiLicenseNumber() {
            return this.fssaiLicenseNumber;
        }

        @java.lang.SuppressWarnings("all")
        public Double getLat() {
            return this.lat;
        }

        @java.lang.SuppressWarnings("all")
        public Double getLng() {
            return this.lng;
        }

        @java.lang.SuppressWarnings("all")
        public List<TimingRequest> getTimings() {
            return this.timings;
        }

        @java.lang.SuppressWarnings("all")
        public String getBannerUrl() {
            return this.bannerUrl;
        }

        @java.lang.SuppressWarnings("all")
        public String getCuisine() {
            return this.cuisine;
        }

        @java.lang.SuppressWarnings("all")
        public Double getRating() {
            return this.rating;
        }

        @java.lang.SuppressWarnings("all")
        public Integer getReviewsCount() {
            return this.reviewsCount;
        }

        @java.lang.SuppressWarnings("all")
        public Integer getDeliveryTime() {
            return this.deliveryTime;
        }

        @java.lang.SuppressWarnings("all")
        public Double getDeliveryFee() {
            return this.deliveryFee;
        }

        @java.lang.SuppressWarnings("all")
        public String getTags() {
            return this.tags;
        }

        @java.lang.SuppressWarnings("all")
        public void setName(final String name) {
            this.name = name;
        }

        @java.lang.SuppressWarnings("all")
        public void setFssaiLicenseNumber(final String fssaiLicenseNumber) {
            this.fssaiLicenseNumber = fssaiLicenseNumber;
        }

        @java.lang.SuppressWarnings("all")
        public void setLat(final Double lat) {
            this.lat = lat;
        }

        @java.lang.SuppressWarnings("all")
        public void setLng(final Double lng) {
            this.lng = lng;
        }

        @java.lang.SuppressWarnings("all")
        public void setTimings(final List<TimingRequest> timings) {
            this.timings = timings;
        }

        @java.lang.SuppressWarnings("all")
        public void setBannerUrl(final String bannerUrl) {
            this.bannerUrl = bannerUrl;
        }

        @java.lang.SuppressWarnings("all")
        public void setCuisine(final String cuisine) {
            this.cuisine = cuisine;
        }

        @java.lang.SuppressWarnings("all")
        public void setRating(final Double rating) {
            this.rating = rating;
        }

        @java.lang.SuppressWarnings("all")
        public void setReviewsCount(final Integer reviewsCount) {
            this.reviewsCount = reviewsCount;
        }

        @java.lang.SuppressWarnings("all")
        public void setDeliveryTime(final Integer deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setDeliveryFee(final Double deliveryFee) {
            this.deliveryFee = deliveryFee;
        }

        @java.lang.SuppressWarnings("all")
        public void setTags(final String tags) {
            this.tags = tags;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OutletOnboardRequest)) return false;
            final OutletOnboardRequest other = (OutletOnboardRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$lat = this.getLat();
            final java.lang.Object other$lat = other.getLat();
            if (this$lat == null ? other$lat != null : !this$lat.equals(other$lat)) return false;
            final java.lang.Object this$lng = this.getLng();
            final java.lang.Object other$lng = other.getLng();
            if (this$lng == null ? other$lng != null : !this$lng.equals(other$lng)) return false;
            final java.lang.Object this$rating = this.getRating();
            final java.lang.Object other$rating = other.getRating();
            if (this$rating == null ? other$rating != null : !this$rating.equals(other$rating)) return false;
            final java.lang.Object this$reviewsCount = this.getReviewsCount();
            final java.lang.Object other$reviewsCount = other.getReviewsCount();
            if (this$reviewsCount == null ? other$reviewsCount != null : !this$reviewsCount.equals(other$reviewsCount)) return false;
            final java.lang.Object this$deliveryTime = this.getDeliveryTime();
            final java.lang.Object other$deliveryTime = other.getDeliveryTime();
            if (this$deliveryTime == null ? other$deliveryTime != null : !this$deliveryTime.equals(other$deliveryTime)) return false;
            final java.lang.Object this$deliveryFee = this.getDeliveryFee();
            final java.lang.Object other$deliveryFee = other.getDeliveryFee();
            if (this$deliveryFee == null ? other$deliveryFee != null : !this$deliveryFee.equals(other$deliveryFee)) return false;
            final java.lang.Object this$name = this.getName();
            final java.lang.Object other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
            final java.lang.Object this$fssaiLicenseNumber = this.getFssaiLicenseNumber();
            final java.lang.Object other$fssaiLicenseNumber = other.getFssaiLicenseNumber();
            if (this$fssaiLicenseNumber == null ? other$fssaiLicenseNumber != null : !this$fssaiLicenseNumber.equals(other$fssaiLicenseNumber)) return false;
            final java.lang.Object this$timings = this.getTimings();
            final java.lang.Object other$timings = other.getTimings();
            if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
            final java.lang.Object this$bannerUrl = this.getBannerUrl();
            final java.lang.Object other$bannerUrl = other.getBannerUrl();
            if (this$bannerUrl == null ? other$bannerUrl != null : !this$bannerUrl.equals(other$bannerUrl)) return false;
            final java.lang.Object this$cuisine = this.getCuisine();
            final java.lang.Object other$cuisine = other.getCuisine();
            if (this$cuisine == null ? other$cuisine != null : !this$cuisine.equals(other$cuisine)) return false;
            final java.lang.Object this$tags = this.getTags();
            final java.lang.Object other$tags = other.getTags();
            if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OutletOnboardRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $lat = this.getLat();
            result = result * PRIME + ($lat == null ? 43 : $lat.hashCode());
            final java.lang.Object $lng = this.getLng();
            result = result * PRIME + ($lng == null ? 43 : $lng.hashCode());
            final java.lang.Object $rating = this.getRating();
            result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
            final java.lang.Object $reviewsCount = this.getReviewsCount();
            result = result * PRIME + ($reviewsCount == null ? 43 : $reviewsCount.hashCode());
            final java.lang.Object $deliveryTime = this.getDeliveryTime();
            result = result * PRIME + ($deliveryTime == null ? 43 : $deliveryTime.hashCode());
            final java.lang.Object $deliveryFee = this.getDeliveryFee();
            result = result * PRIME + ($deliveryFee == null ? 43 : $deliveryFee.hashCode());
            final java.lang.Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            final java.lang.Object $fssaiLicenseNumber = this.getFssaiLicenseNumber();
            result = result * PRIME + ($fssaiLicenseNumber == null ? 43 : $fssaiLicenseNumber.hashCode());
            final java.lang.Object $timings = this.getTimings();
            result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
            final java.lang.Object $bannerUrl = this.getBannerUrl();
            result = result * PRIME + ($bannerUrl == null ? 43 : $bannerUrl.hashCode());
            final java.lang.Object $cuisine = this.getCuisine();
            result = result * PRIME + ($cuisine == null ? 43 : $cuisine.hashCode());
            final java.lang.Object $tags = this.getTags();
            result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OutletOnboardRequest(name=" + this.getName() + ", fssaiLicenseNumber=" + this.getFssaiLicenseNumber() + ", lat=" + this.getLat() + ", lng=" + this.getLng() + ", timings=" + this.getTimings() + ", bannerUrl=" + this.getBannerUrl() + ", cuisine=" + this.getCuisine() + ", rating=" + this.getRating() + ", reviewsCount=" + this.getReviewsCount() + ", deliveryTime=" + this.getDeliveryTime() + ", deliveryFee=" + this.getDeliveryFee() + ", tags=" + this.getTags() + ")";
        }
    }
