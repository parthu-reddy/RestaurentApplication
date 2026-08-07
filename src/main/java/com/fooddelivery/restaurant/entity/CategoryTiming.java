package com.fooddelivery.restaurant.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "category_timings")
public class CategoryTiming {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;
    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;
    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;
    @Version
    @Column(name = "version")
    private Integer version;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @java.lang.SuppressWarnings("all")
    public static class CategoryTimingBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private Category category;
        @java.lang.SuppressWarnings("all")
        private LocalTime openingTime;
        @java.lang.SuppressWarnings("all")
        private LocalTime closingTime;
        @java.lang.SuppressWarnings("all")
        private Integer version;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime createdAt;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime updatedAt;

        @java.lang.SuppressWarnings("all")
        CategoryTimingBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonIgnore
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder category(final Category category) {
            this.category = category;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder openingTime(final LocalTime openingTime) {
            this.openingTime = openingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder closingTime(final LocalTime closingTime) {
            this.closingTime = closingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryTiming.CategoryTimingBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public CategoryTiming build() {
            return new CategoryTiming(this.id, this.category, this.openingTime, this.closingTime, this.version, this.createdAt, this.updatedAt);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "CategoryTiming.CategoryTimingBuilder(id=" + this.id + ", category=" + this.category + ", openingTime=" + this.openingTime + ", closingTime=" + this.closingTime + ", version=" + this.version + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static CategoryTiming.CategoryTimingBuilder builder() {
        return new CategoryTiming.CategoryTimingBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
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
    public Integer getVersion() {
        return this.version;
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
    public void setId(final UUID id) {
        this.id = id;
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
    public void setVersion(final Integer version) {
        this.version = version;
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
        if (!(o instanceof CategoryTiming)) return false;
        final CategoryTiming other = (CategoryTiming) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$category = this.getCategory();
        final java.lang.Object other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
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
        return other instanceof CategoryTiming;
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
        final java.lang.Object $category = this.getCategory();
        result = result * PRIME + ($category == null ? 43 : $category.hashCode());
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
        return "CategoryTiming(id=" + this.getId() + ", category=" + this.getCategory() + ", openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ", version=" + this.getVersion() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public CategoryTiming() {
    }

    @java.lang.SuppressWarnings("all")
    public CategoryTiming(final UUID id, final Category category, final LocalTime openingTime, final LocalTime closingTime, final Integer version, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.id = id;
        this.category = category;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
