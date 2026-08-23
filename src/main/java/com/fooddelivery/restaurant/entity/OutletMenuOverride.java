package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "outlet_menu_overrides")
public class OutletMenuOverride {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "outlet_id")
    private UUID outletId;
    @Column(name = "master_menu_item_id")
    private UUID masterMenuItemId;
    @Positive
    @Column(name = "overridden_price")
    private BigDecimal overriddenPrice;
    @NotNull
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Positive
    @Column(name = "overridden_prep_time_minutes")
    private Integer overriddenPrepTimeMinutes;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;


public static class OutletMenuOverrideBuilder {
private UUID id;
private UUID outletId;
private UUID masterMenuItemId;
private BigDecimal overriddenPrice;
private Boolean isAvailable;
private Integer overriddenPrepTimeMinutes;
private Integer version;

OutletMenuOverrideBuilder() {
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder outletId(final UUID outletId) {
            this.outletId = outletId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder masterMenuItemId(final UUID masterMenuItemId) {
            this.masterMenuItemId = masterMenuItemId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder overriddenPrice(final BigDecimal overriddenPrice) {
            this.overriddenPrice = overriddenPrice;
            return this;
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder isAvailable(final Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder overriddenPrepTimeMinutes(final Integer overriddenPrepTimeMinutes) {
            this.overriddenPrepTimeMinutes = overriddenPrepTimeMinutes;
            return this;
        }

        /**
         * @return {@code this}.
         */
public OutletMenuOverride.OutletMenuOverrideBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

public OutletMenuOverride build() {
            return new OutletMenuOverride(this.id, this.outletId, this.masterMenuItemId, this.overriddenPrice, this.isAvailable, this.overriddenPrepTimeMinutes, this.version);
        }

        @java.lang.Override
public java.lang.String toString() {
            return "OutletMenuOverride.OutletMenuOverrideBuilder(id=" + this.id + ", outletId=" + this.outletId + ", masterMenuItemId=" + this.masterMenuItemId + ", overriddenPrice=" + this.overriddenPrice + ", isAvailable=" + this.isAvailable + ", overriddenPrepTimeMinutes=" + this.overriddenPrepTimeMinutes + ", version=" + this.version + ")";
        }
    }

public static OutletMenuOverride.OutletMenuOverrideBuilder builder() {
        return new OutletMenuOverride.OutletMenuOverrideBuilder();
    }

public UUID getId() {
        return this.id;
    }

public UUID getOutletId() {
        return this.outletId;
    }

public UUID getMasterMenuItemId() {
        return this.masterMenuItemId;
    }

public BigDecimal getOverriddenPrice() {
        return this.overriddenPrice;
    }

public Boolean getIsAvailable() {
        return this.isAvailable;
    }

public Integer getOverriddenPrepTimeMinutes() {
        return this.overriddenPrepTimeMinutes;
    }

public Integer getVersion() {
        return this.version;
    }

public void setId(final UUID id) {
        this.id = id;
    }

public void setOutletId(final UUID outletId) {
        this.outletId = outletId;
    }

public void setMasterMenuItemId(final UUID masterMenuItemId) {
        this.masterMenuItemId = masterMenuItemId;
    }

public void setOverriddenPrice(final BigDecimal overriddenPrice) {
        this.overriddenPrice = overriddenPrice;
    }

public void setIsAvailable(final Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

public void setOverriddenPrepTimeMinutes(final Integer overriddenPrepTimeMinutes) {
        this.overriddenPrepTimeMinutes = overriddenPrepTimeMinutes;
    }

public void setVersion(final Integer version) {
        this.version = version;
    }

    @java.lang.Override
public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OutletMenuOverride)) return false;
        final OutletMenuOverride other = (OutletMenuOverride) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$isAvailable = this.getIsAvailable();
        final java.lang.Object other$isAvailable = other.getIsAvailable();
        if (this$isAvailable == null ? other$isAvailable != null : !this$isAvailable.equals(other$isAvailable)) return false;
        final java.lang.Object this$overriddenPrepTimeMinutes = this.getOverriddenPrepTimeMinutes();
        final java.lang.Object other$overriddenPrepTimeMinutes = other.getOverriddenPrepTimeMinutes();
        if (this$overriddenPrepTimeMinutes == null ? other$overriddenPrepTimeMinutes != null : !this$overriddenPrepTimeMinutes.equals(other$overriddenPrepTimeMinutes)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$outletId = this.getOutletId();
        final java.lang.Object other$outletId = other.getOutletId();
        if (this$outletId == null ? other$outletId != null : !this$outletId.equals(other$outletId)) return false;
        final java.lang.Object this$masterMenuItemId = this.getMasterMenuItemId();
        final java.lang.Object other$masterMenuItemId = other.getMasterMenuItemId();
        if (this$masterMenuItemId == null ? other$masterMenuItemId != null : !this$masterMenuItemId.equals(other$masterMenuItemId)) return false;
        final java.lang.Object this$overriddenPrice = this.getOverriddenPrice();
        final java.lang.Object other$overriddenPrice = other.getOverriddenPrice();
        if (this$overriddenPrice == null ? other$overriddenPrice != null : !this$overriddenPrice.equals(other$overriddenPrice)) return false;
        return true;
    }

protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OutletMenuOverride;
    }

    @java.lang.Override
public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $isAvailable = this.getIsAvailable();
        result = result * PRIME + ($isAvailable == null ? 43 : $isAvailable.hashCode());
        final java.lang.Object $overriddenPrepTimeMinutes = this.getOverriddenPrepTimeMinutes();
        result = result * PRIME + ($overriddenPrepTimeMinutes == null ? 43 : $overriddenPrepTimeMinutes.hashCode());
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $outletId = this.getOutletId();
        result = result * PRIME + ($outletId == null ? 43 : $outletId.hashCode());
        final java.lang.Object $masterMenuItemId = this.getMasterMenuItemId();
        result = result * PRIME + ($masterMenuItemId == null ? 43 : $masterMenuItemId.hashCode());
        final java.lang.Object $overriddenPrice = this.getOverriddenPrice();
        result = result * PRIME + ($overriddenPrice == null ? 43 : $overriddenPrice.hashCode());
        return result;
    }

    @java.lang.Override
public java.lang.String toString() {
        return "OutletMenuOverride(id=" + this.getId() + ", outletId=" + this.getOutletId() + ", masterMenuItemId=" + this.getMasterMenuItemId() + ", overriddenPrice=" + this.getOverriddenPrice() + ", isAvailable=" + this.getIsAvailable() + ", overriddenPrepTimeMinutes=" + this.getOverriddenPrepTimeMinutes() + ", version=" + this.getVersion() + ")";
    }

public OutletMenuOverride() {
    }

public OutletMenuOverride(final UUID id, final UUID outletId, final UUID masterMenuItemId, final BigDecimal overriddenPrice, final Boolean isAvailable, final Integer overriddenPrepTimeMinutes, final Integer version) {
        this.id = id;
        this.outletId = outletId;
        this.masterMenuItemId = masterMenuItemId;
        this.overriddenPrice = overriddenPrice;
        this.isAvailable = isAvailable;
        this.overriddenPrepTimeMinutes = overriddenPrepTimeMinutes;
        this.version = version;
    }
}
