package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "outlets")
public class Outlet {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "brand_id")
    private UUID brandId;
    @Column(name = "name")
    private String name;
    @Column(name = "fssai_license_number", unique = true)
    private String fssaiLicenseNumber;
    @JsonIgnore
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private org.locationtech.jts.geom.Point location;
    @Column(name = "banner_url")
    private String bannerUrl;
    @jakarta.persistence.OneToMany(mappedBy = "outlet", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OutletTiming> timings = new java.util.ArrayList<>();
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "default_prep_time_seconds")
    private Integer defaultPrepTimeSeconds;
    @Column(name = "cuisine")
    private String cuisine;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "reviews_count")
    private Integer reviewsCount;
    @Column(name = "delivery_time")
    private Integer deliveryTime;
    @Column(name = "delivery_fee")
    private java.math.BigDecimal deliveryFee;
    @Column(name = "tags")
    private String tags;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;


public static class OutletBuilder {
private UUID id;
private UUID brandId;
private String name;
private String fssaiLicenseNumber;
private org.locationtech.jts.geom.Point location;
private String bannerUrl;
private java.util.List<OutletTiming> timings;
private Boolean isActive;
private Integer defaultPrepTimeSeconds;
private String cuisine;
private Double rating;
private Integer reviewsCount;
private Integer deliveryTime;
private java.math.BigDecimal deliveryFee;
private String tags;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private Integer version;

OutletBuilder() {
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder brandId(final UUID brandId) {
            this.brandId = brandId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder fssaiLicenseNumber(final String fssaiLicenseNumber) {
            this.fssaiLicenseNumber = fssaiLicenseNumber;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonIgnore
public Outlet.OutletBuilder location(final org.locationtech.jts.geom.Point location) {
            this.location = location;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder bannerUrl(final String bannerUrl) {
            this.bannerUrl = bannerUrl;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder timings(final java.util.List<OutletTiming> timings) {
            this.timings = timings;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder isActive(final Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder defaultPrepTimeSeconds(final Integer defaultPrepTimeSeconds) {
            this.defaultPrepTimeSeconds = defaultPrepTimeSeconds;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder cuisine(final String cuisine) {
            this.cuisine = cuisine;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder rating(final Double rating) {
            this.rating = rating;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder reviewsCount(final Integer reviewsCount) {
            this.reviewsCount = reviewsCount;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder deliveryTime(final Integer deliveryTime) {
            this.deliveryTime = deliveryTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder deliveryFee(final java.math.BigDecimal deliveryFee) {
            this.deliveryFee = deliveryFee;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder tags(final String tags) {
            this.tags = tags;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
public Outlet.OutletBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

public Outlet build() {
            return new Outlet(this.id, this.brandId, this.name, this.fssaiLicenseNumber, this.location, this.bannerUrl, this.timings, this.isActive, this.defaultPrepTimeSeconds, this.cuisine, this.rating, this.reviewsCount, this.deliveryTime, this.deliveryFee, this.tags, this.createdAt, this.updatedAt, this.version);
        }

        @java.lang.Override
public java.lang.String toString() {
            return "Outlet.OutletBuilder(id=" + this.id + ", brandId=" + this.brandId + ", name=" + this.name + ", fssaiLicenseNumber=" + this.fssaiLicenseNumber + ", location=" + this.location + ", bannerUrl=" + this.bannerUrl + ", timings=" + this.timings + ", isActive=" + this.isActive + ", defaultPrepTimeSeconds=" + this.defaultPrepTimeSeconds + ", cuisine=" + this.cuisine + ", rating=" + this.rating + ", reviewsCount=" + this.reviewsCount + ", deliveryTime=" + this.deliveryTime + ", deliveryFee=" + this.deliveryFee + ", tags=" + this.tags + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", version=" + this.version + ")";
        }
    }

public static Outlet.OutletBuilder builder() {
        return new Outlet.OutletBuilder();
    }

public UUID getId() {
        return this.id;
    }

public UUID getBrandId() {
        return this.brandId;
    }

public String getName() {
        return this.name;
    }

public String getFssaiLicenseNumber() {
        return this.fssaiLicenseNumber;
    }

public org.locationtech.jts.geom.Point getLocation() {
        return this.location;
    }

public String getBannerUrl() {
        return this.bannerUrl;
    }

public java.util.List<OutletTiming> getTimings() {
        return this.timings;
    }

public Boolean getIsActive() {
        return this.isActive;
    }

public Integer getDefaultPrepTimeSeconds() {
        return this.defaultPrepTimeSeconds;
    }

public String getCuisine() {
        return this.cuisine;
    }

public Double getRating() {
        return this.rating;
    }

public Integer getReviewsCount() {
        return this.reviewsCount;
    }

public Integer getDeliveryTime() {
        return this.deliveryTime;
    }

public java.math.BigDecimal getDeliveryFee() {
        return this.deliveryFee;
    }

public String getTags() {
        return this.tags;
    }

public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

public Integer getVersion() {
        return this.version;
    }

public void setId(final UUID id) {
        this.id = id;
    }

public void setBrandId(final UUID brandId) {
        this.brandId = brandId;
    }

public void setName(final String name) {
        this.name = name;
    }

public void setFssaiLicenseNumber(final String fssaiLicenseNumber) {
        this.fssaiLicenseNumber = fssaiLicenseNumber;
    }

    @JsonIgnore
public void setLocation(final org.locationtech.jts.geom.Point location) {
        this.location = location;
    }

public void setBannerUrl(final String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

public void setTimings(final java.util.List<OutletTiming> timings) {
        this.timings = timings;
    }

public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

public void setDefaultPrepTimeSeconds(final Integer defaultPrepTimeSeconds) {
        this.defaultPrepTimeSeconds = defaultPrepTimeSeconds;
    }

public void setCuisine(final String cuisine) {
        this.cuisine = cuisine;
    }

public void setRating(final Double rating) {
        this.rating = rating;
    }

public void setReviewsCount(final Integer reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

public void setDeliveryTime(final Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

public void setDeliveryFee(final java.math.BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

public void setTags(final String tags) {
        this.tags = tags;
    }

public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

public void setVersion(final Integer version) {
        this.version = version;
    }

    @java.lang.Override
public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Outlet)) return false;
        final Outlet other = (Outlet) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$isActive = this.getIsActive();
        final java.lang.Object other$isActive = other.getIsActive();
        if (this$isActive == null ? other$isActive != null : !this$isActive.equals(other$isActive)) return false;
        final java.lang.Object this$defaultPrepTimeSeconds = this.getDefaultPrepTimeSeconds();
        final java.lang.Object other$defaultPrepTimeSeconds = other.getDefaultPrepTimeSeconds();
        if (this$defaultPrepTimeSeconds == null ? other$defaultPrepTimeSeconds != null : !this$defaultPrepTimeSeconds.equals(other$defaultPrepTimeSeconds)) return false;
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
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$brandId = this.getBrandId();
        final java.lang.Object other$brandId = other.getBrandId();
        if (this$brandId == null ? other$brandId != null : !this$brandId.equals(other$brandId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$fssaiLicenseNumber = this.getFssaiLicenseNumber();
        final java.lang.Object other$fssaiLicenseNumber = other.getFssaiLicenseNumber();
        if (this$fssaiLicenseNumber == null ? other$fssaiLicenseNumber != null : !this$fssaiLicenseNumber.equals(other$fssaiLicenseNumber)) return false;
        final java.lang.Object this$location = this.getLocation();
        final java.lang.Object other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) return false;
        final java.lang.Object this$bannerUrl = this.getBannerUrl();
        final java.lang.Object other$bannerUrl = other.getBannerUrl();
        if (this$bannerUrl == null ? other$bannerUrl != null : !this$bannerUrl.equals(other$bannerUrl)) return false;
        final java.lang.Object this$timings = this.getTimings();
        final java.lang.Object other$timings = other.getTimings();
        if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
        final java.lang.Object this$cuisine = this.getCuisine();
        final java.lang.Object other$cuisine = other.getCuisine();
        if (this$cuisine == null ? other$cuisine != null : !this$cuisine.equals(other$cuisine)) return false;
        final java.lang.Object this$tags = this.getTags();
        final java.lang.Object other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$updatedAt = this.getUpdatedAt();
        final java.lang.Object other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
        return true;
    }

protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Outlet;
    }

    @java.lang.Override
public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $isActive = this.getIsActive();
        result = result * PRIME + ($isActive == null ? 43 : $isActive.hashCode());
        final java.lang.Object $defaultPrepTimeSeconds = this.getDefaultPrepTimeSeconds();
        result = result * PRIME + ($defaultPrepTimeSeconds == null ? 43 : $defaultPrepTimeSeconds.hashCode());
        final java.lang.Object $rating = this.getRating();
        result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
        final java.lang.Object $reviewsCount = this.getReviewsCount();
        result = result * PRIME + ($reviewsCount == null ? 43 : $reviewsCount.hashCode());
        final java.lang.Object $deliveryTime = this.getDeliveryTime();
        result = result * PRIME + ($deliveryTime == null ? 43 : $deliveryTime.hashCode());
        final java.lang.Object $deliveryFee = this.getDeliveryFee();
        result = result * PRIME + ($deliveryFee == null ? 43 : $deliveryFee.hashCode());
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $brandId = this.getBrandId();
        result = result * PRIME + ($brandId == null ? 43 : $brandId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $fssaiLicenseNumber = this.getFssaiLicenseNumber();
        result = result * PRIME + ($fssaiLicenseNumber == null ? 43 : $fssaiLicenseNumber.hashCode());
        final java.lang.Object $location = this.getLocation();
        result = result * PRIME + ($location == null ? 43 : $location.hashCode());
        final java.lang.Object $bannerUrl = this.getBannerUrl();
        result = result * PRIME + ($bannerUrl == null ? 43 : $bannerUrl.hashCode());
        final java.lang.Object $timings = this.getTimings();
        result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
        final java.lang.Object $cuisine = this.getCuisine();
        result = result * PRIME + ($cuisine == null ? 43 : $cuisine.hashCode());
        final java.lang.Object $tags = this.getTags();
        result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
public java.lang.String toString() {
        return "Outlet(id=" + this.getId() + ", brandId=" + this.getBrandId() + ", name=" + this.getName() + ", fssaiLicenseNumber=" + this.getFssaiLicenseNumber() + ", location=" + this.getLocation() + ", bannerUrl=" + this.getBannerUrl() + ", timings=" + this.getTimings() + ", isActive=" + this.getIsActive() + ", defaultPrepTimeSeconds=" + this.getDefaultPrepTimeSeconds() + ", cuisine=" + this.getCuisine() + ", rating=" + this.getRating() + ", reviewsCount=" + this.getReviewsCount() + ", deliveryTime=" + this.getDeliveryTime() + ", deliveryFee=" + this.getDeliveryFee() + ", tags=" + this.getTags() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", version=" + this.getVersion() + ")";
    }

public Outlet() {
    }

public Outlet(final UUID id, final UUID brandId, final String name, final String fssaiLicenseNumber, final org.locationtech.jts.geom.Point location, final String bannerUrl, final java.util.List<OutletTiming> timings, final Boolean isActive, final Integer defaultPrepTimeSeconds, final String cuisine, final Double rating, final Integer reviewsCount, final Integer deliveryTime, final java.math.BigDecimal deliveryFee, final String tags, final LocalDateTime createdAt, final LocalDateTime updatedAt, final Integer version) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
        this.fssaiLicenseNumber = fssaiLicenseNumber;
        this.location = location;
        this.bannerUrl = bannerUrl;
        this.timings = timings;
        this.isActive = isActive;
        this.defaultPrepTimeSeconds = defaultPrepTimeSeconds;
        this.cuisine = cuisine;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
        this.deliveryTime = deliveryTime;
        this.deliveryFee = deliveryFee;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
