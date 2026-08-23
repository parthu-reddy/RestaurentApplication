package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

@Entity
@Table(name = "master_menu_items")
public class MasterMenuItem {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "brand_id")
    private UUID brandId;
    @Column(name = "category_id")
    private UUID categoryId;
    @NotBlank
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @NotNull
    @Positive
    @Column(name = "base_price")
    private BigDecimal basePrice;
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax(value = "10.0", inclusive = false)
    @Column(name = "packing_charge")
    private BigDecimal packingCharge;
    @Positive
    @Column(name = "default_prep_time_minutes")
    private Integer defaultPrepTimeMinutes;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;

private static BigDecimal $default$packingCharge() {
        return BigDecimal.ZERO;
    }


public static class MasterMenuItemBuilder {
private UUID id;
private UUID brandId;
private UUID categoryId;
private String name;
private String description;
private String imageUrl;
private BigDecimal basePrice;
private boolean packingCharge$set;
private BigDecimal packingCharge$value;
private Integer defaultPrepTimeMinutes;
private Integer version;

MasterMenuItemBuilder() {
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder brandId(final UUID brandId) {
            this.brandId = brandId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder categoryId(final UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder imageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder basePrice(final BigDecimal basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder packingCharge(final BigDecimal packingCharge) {
            this.packingCharge$value = packingCharge;
            packingCharge$set = true;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder defaultPrepTimeMinutes(final Integer defaultPrepTimeMinutes) {
            this.defaultPrepTimeMinutes = defaultPrepTimeMinutes;
            return this;
        }

        /**
         * @return {@code this}.
         */
public MasterMenuItem.MasterMenuItemBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

public MasterMenuItem build() {
            BigDecimal packingCharge$value = this.packingCharge$value;
            if (!this.packingCharge$set) packingCharge$value = MasterMenuItem.$default$packingCharge();
            return new MasterMenuItem(this.id, this.brandId, this.categoryId, this.name, this.description, this.imageUrl, this.basePrice, packingCharge$value, this.defaultPrepTimeMinutes, this.version);
        }

        @java.lang.Override
public java.lang.String toString() {
            return "MasterMenuItem.MasterMenuItemBuilder(id=" + this.id + ", brandId=" + this.brandId + ", categoryId=" + this.categoryId + ", name=" + this.name + ", description=" + this.description + ", imageUrl=" + this.imageUrl + ", basePrice=" + this.basePrice + ", packingCharge$value=" + this.packingCharge$value + ", defaultPrepTimeMinutes=" + this.defaultPrepTimeMinutes + ", version=" + this.version + ")";
        }
    }

public static MasterMenuItem.MasterMenuItemBuilder builder() {
        return new MasterMenuItem.MasterMenuItemBuilder();
    }

public UUID getId() {
        return this.id;
    }

public UUID getBrandId() {
        return this.brandId;
    }

public UUID getCategoryId() {
        return this.categoryId;
    }

public String getName() {
        return this.name;
    }

public String getDescription() {
        return this.description;
    }

public String getImageUrl() {
        return this.imageUrl;
    }

public BigDecimal getBasePrice() {
        return this.basePrice;
    }

public BigDecimal getPackingCharge() {
        return this.packingCharge;
    }

public Integer getDefaultPrepTimeMinutes() {
        return this.defaultPrepTimeMinutes;
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

public void setCategoryId(final UUID categoryId) {
        this.categoryId = categoryId;
    }

public void setName(final String name) {
        this.name = name;
    }

public void setDescription(final String description) {
        this.description = description;
    }

public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

public void setBasePrice(final BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

public void setPackingCharge(final BigDecimal packingCharge) {
        this.packingCharge = packingCharge;
    }

public void setDefaultPrepTimeMinutes(final Integer defaultPrepTimeMinutes) {
        this.defaultPrepTimeMinutes = defaultPrepTimeMinutes;
    }

public void setVersion(final Integer version) {
        this.version = version;
    }

    @java.lang.Override
public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof MasterMenuItem)) return false;
        final MasterMenuItem other = (MasterMenuItem) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$defaultPrepTimeMinutes = this.getDefaultPrepTimeMinutes();
        final java.lang.Object other$defaultPrepTimeMinutes = other.getDefaultPrepTimeMinutes();
        if (this$defaultPrepTimeMinutes == null ? other$defaultPrepTimeMinutes != null : !this$defaultPrepTimeMinutes.equals(other$defaultPrepTimeMinutes)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$brandId = this.getBrandId();
        final java.lang.Object other$brandId = other.getBrandId();
        if (this$brandId == null ? other$brandId != null : !this$brandId.equals(other$brandId)) return false;
        final java.lang.Object this$categoryId = this.getCategoryId();
        final java.lang.Object other$categoryId = other.getCategoryId();
        if (this$categoryId == null ? other$categoryId != null : !this$categoryId.equals(other$categoryId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$description = this.getDescription();
        final java.lang.Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
        final java.lang.Object this$imageUrl = this.getImageUrl();
        final java.lang.Object other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) return false;
        final java.lang.Object this$basePrice = this.getBasePrice();
        final java.lang.Object other$basePrice = other.getBasePrice();
        if (this$basePrice == null ? other$basePrice != null : !this$basePrice.equals(other$basePrice)) return false;
        final java.lang.Object this$packingCharge = this.getPackingCharge();
        final java.lang.Object other$packingCharge = other.getPackingCharge();
        if (this$packingCharge == null ? other$packingCharge != null : !this$packingCharge.equals(other$packingCharge)) return false;
        return true;
    }

protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MasterMenuItem;
    }

    @java.lang.Override
public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $defaultPrepTimeMinutes = this.getDefaultPrepTimeMinutes();
        result = result * PRIME + ($defaultPrepTimeMinutes == null ? 43 : $defaultPrepTimeMinutes.hashCode());
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $brandId = this.getBrandId();
        result = result * PRIME + ($brandId == null ? 43 : $brandId.hashCode());
        final java.lang.Object $categoryId = this.getCategoryId();
        result = result * PRIME + ($categoryId == null ? 43 : $categoryId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final java.lang.Object $imageUrl = this.getImageUrl();
        result = result * PRIME + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        final java.lang.Object $basePrice = this.getBasePrice();
        result = result * PRIME + ($basePrice == null ? 43 : $basePrice.hashCode());
        final java.lang.Object $packingCharge = this.getPackingCharge();
        result = result * PRIME + ($packingCharge == null ? 43 : $packingCharge.hashCode());
        return result;
    }

    @java.lang.Override
public java.lang.String toString() {
        return "MasterMenuItem(id=" + this.getId() + ", brandId=" + this.getBrandId() + ", categoryId=" + this.getCategoryId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", imageUrl=" + this.getImageUrl() + ", basePrice=" + this.getBasePrice() + ", packingCharge=" + this.getPackingCharge() + ", defaultPrepTimeMinutes=" + this.getDefaultPrepTimeMinutes() + ", version=" + this.getVersion() + ")";
    }

public MasterMenuItem() {
        this.packingCharge = MasterMenuItem.$default$packingCharge();
    }

public MasterMenuItem(final UUID id, final UUID brandId, final UUID categoryId, final String name, final String description, final String imageUrl, final BigDecimal basePrice, final BigDecimal packingCharge, final Integer defaultPrepTimeMinutes, final Integer version) {
        this.id = id;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.basePrice = basePrice;
        this.packingCharge = packingCharge;
        this.defaultPrepTimeMinutes = defaultPrepTimeMinutes;
        this.version = version;
    }
}
