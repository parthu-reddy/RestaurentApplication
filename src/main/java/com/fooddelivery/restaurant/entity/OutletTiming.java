package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "outlet_timings")
public class OutletTiming {
    @Id
    @Column(name = "id")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outlet_id", nullable = false)
    @JsonIgnore
    private Outlet outlet;
    @Column(name = "opening_time")
    private LocalTime openingTime;
    @Column(name = "closing_time")
    private LocalTime closingTime;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;


    @java.lang.SuppressWarnings("all")
    public static class OutletTimingBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private Outlet outlet;
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
        OutletTimingBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonIgnore
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder outlet(final Outlet outlet) {
            this.outlet = outlet;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder openingTime(final LocalTime openingTime) {
            this.openingTime = openingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder closingTime(final LocalTime closingTime) {
            this.closingTime = closingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OutletTiming.OutletTimingBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OutletTiming build() {
            return new OutletTiming(this.id, this.outlet, this.openingTime, this.closingTime, this.createdAt, this.updatedAt, this.version);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OutletTiming.OutletTimingBuilder(id=" + this.id + ", outlet=" + this.outlet + ", openingTime=" + this.openingTime + ", closingTime=" + this.closingTime + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", version=" + this.version + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OutletTiming.OutletTimingBuilder builder() {
        return new OutletTiming.OutletTimingBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public Outlet getOutlet() {
        return this.outlet;
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

    @JsonIgnore
    @java.lang.SuppressWarnings("all")
    public void setOutlet(final Outlet outlet) {
        this.outlet = outlet;
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
    public OutletTiming() {
    }

    @java.lang.SuppressWarnings("all")
    public OutletTiming(final UUID id, final Outlet outlet, final LocalTime openingTime, final LocalTime closingTime, final LocalDateTime createdAt, final LocalDateTime updatedAt, final Integer version) {
        this.id = id;
        this.outlet = outlet;
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
        if (!(o instanceof OutletTiming)) return false;
        final OutletTiming other = (OutletTiming) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
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
        return other instanceof OutletTiming;
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
        return "OutletTiming(id=" + this.getId() + ", openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", version=" + this.getVersion() + ")";
    }
}
