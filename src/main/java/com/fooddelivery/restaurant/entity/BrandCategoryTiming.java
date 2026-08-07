package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "brand_category_timings")
public class BrandCategoryTiming {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "brand_id", nullable = false)
    private UUID brandId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;
    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;
    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;


    @java.lang.SuppressWarnings("all")
    public static class BrandCategoryTimingBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private UUID brandId;
        @java.lang.SuppressWarnings("all")
        private Category category;
        @java.lang.SuppressWarnings("all")
        private LocalTime openingTime;
        @java.lang.SuppressWarnings("all")
        private LocalTime closingTime;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime createdAt;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime updatedAt;
        @java.lang.SuppressWarnings("all")
        private Integer version;

        @java.lang.SuppressWarnings("all")
        BrandCategoryTimingBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder brandId(final UUID brandId) {
            this.brandId = brandId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonIgnore
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder category(final Category category) {
            this.category = category;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder openingTime(final LocalTime openingTime) {
            this.openingTime = openingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder closingTime(final LocalTime closingTime) {
            this.closingTime = closingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming.BrandCategoryTimingBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public BrandCategoryTiming build() {
            return new BrandCategoryTiming(this.id, this.brandId, this.category, this.openingTime, this.closingTime, this.createdAt, this.updatedAt, this.version);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "BrandCategoryTiming.BrandCategoryTimingBuilder(id=" + this.id + ", brandId=" + this.brandId + ", category=" + this.category + ", openingTime=" + this.openingTime + ", closingTime=" + this.closingTime + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", version=" + this.version + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static BrandCategoryTiming.BrandCategoryTimingBuilder builder() {
        return new BrandCategoryTiming.BrandCategoryTimingBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getBrandId() {
        return this.brandId;
    }

    @java.lang.SuppressWarnings("all")
    public Category getCategory() {
        return this.category;
    }

    @java.lang.SuppressWarnings("all")
    public LocalTime getOpeningTime() {
        return this.openingTime;
    }

    @java.lang.SuppressWarnings("all")
    public LocalTime getClosingTime() {
        return this.closingTime;
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
    public Integer getVersion() {
        return this.version;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setBrandId(final UUID brandId) {
        this.brandId = brandId;
    }

    @JsonIgnore
    @java.lang.SuppressWarnings("all")
    public void setCategory(final Category category) {
        this.category = category;
    }

    @java.lang.SuppressWarnings("all")
    public void setOpeningTime(final LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setClosingTime(final LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Integer version) {
        this.version = version;
    }

    @java.lang.SuppressWarnings("all")
    public BrandCategoryTiming() {
    }

    @java.lang.SuppressWarnings("all")
    public BrandCategoryTiming(final UUID id, final UUID brandId, final Category category, final LocalTime openingTime, final LocalTime closingTime, final LocalDateTime createdAt, final LocalDateTime updatedAt, final Integer version) {
        this.id = id;
        this.brandId = brandId;
        this.category = category;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof BrandCategoryTiming)) return false;
        final BrandCategoryTiming other = (BrandCategoryTiming) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$brandId = this.getBrandId();
        final java.lang.Object other$brandId = other.getBrandId();
        if (this$brandId == null ? other$brandId != null : !this$brandId.equals(other$brandId)) return false;
        final java.lang.Object this$openingTime = this.getOpeningTime();
        final java.lang.Object other$openingTime = other.getOpeningTime();
        if (this$openingTime == null ? other$openingTime != null : !this$openingTime.equals(other$openingTime)) return false;
        final java.lang.Object this$closingTime = this.getClosingTime();
        final java.lang.Object other$closingTime = other.getClosingTime();
        if (this$closingTime == null ? other$closingTime != null : !this$closingTime.equals(other$closingTime)) return false;
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
        return other instanceof BrandCategoryTiming;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $brandId = this.getBrandId();
        result = result * PRIME + ($brandId == null ? 43 : $brandId.hashCode());
        final java.lang.Object $openingTime = this.getOpeningTime();
        result = result * PRIME + ($openingTime == null ? 43 : $openingTime.hashCode());
        final java.lang.Object $closingTime = this.getClosingTime();
        result = result * PRIME + ($closingTime == null ? 43 : $closingTime.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "BrandCategoryTiming(id=" + this.getId() + ", brandId=" + this.getBrandId() + ", openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", version=" + this.getVersion() + ")";
    }
}
