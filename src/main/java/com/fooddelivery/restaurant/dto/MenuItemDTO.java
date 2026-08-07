package com.fooddelivery.restaurant.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.io.Serializable;

public class MenuItemDTO implements Serializable {
    private UUID id;
    private UUID restaurantId; // outletId
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer prepTimeMinutes;
    private String imageUrl;
    private UUID categoryId;
    private String categoryName;


    @java.lang.SuppressWarnings("all")
    public static class MenuItemDTOBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private UUID restaurantId;
        @java.lang.SuppressWarnings("all")
        private String name;
        @java.lang.SuppressWarnings("all")
        private String description;
        @java.lang.SuppressWarnings("all")
        private BigDecimal price;
        @java.lang.SuppressWarnings("all")
        private Boolean isAvailable;
        @java.lang.SuppressWarnings("all")
        private Integer prepTimeMinutes;
        @java.lang.SuppressWarnings("all")
        private String imageUrl;
        @java.lang.SuppressWarnings("all")
        private UUID categoryId;
        @java.lang.SuppressWarnings("all")
        private String categoryName;

        @java.lang.SuppressWarnings("all")
        MenuItemDTOBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder restaurantId(final UUID restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder price(final BigDecimal price) {
            this.price = price;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder isAvailable(final Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder prepTimeMinutes(final Integer prepTimeMinutes) {
            this.prepTimeMinutes = prepTimeMinutes;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder imageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder categoryId(final UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public MenuItemDTO.MenuItemDTOBuilder categoryName(final String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public MenuItemDTO build() {
            return new MenuItemDTO(this.id, this.restaurantId, this.name, this.description, this.price, this.isAvailable, this.prepTimeMinutes, this.imageUrl, this.categoryId, this.categoryName);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "MenuItemDTO.MenuItemDTOBuilder(id=" + this.id + ", restaurantId=" + this.restaurantId + ", name=" + this.name + ", description=" + this.description + ", price=" + this.price + ", isAvailable=" + this.isAvailable + ", prepTimeMinutes=" + this.prepTimeMinutes + ", imageUrl=" + this.imageUrl + ", categoryId=" + this.categoryId + ", categoryName=" + this.categoryName + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static MenuItemDTO.MenuItemDTOBuilder builder() {
        return new MenuItemDTO.MenuItemDTOBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getRestaurantId() {
        return this.restaurantId;
    }

    @java.lang.SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public String getDescription() {
        return this.description;
    }

    @java.lang.SuppressWarnings("all")
    public BigDecimal getPrice() {
        return this.price;
    }

    @java.lang.SuppressWarnings("all")
    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getPrepTimeMinutes() {
        return this.prepTimeMinutes;
    }

    @java.lang.SuppressWarnings("all")
    public String getImageUrl() {
        return this.imageUrl;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getCategoryId() {
        return this.categoryId;
    }

    @java.lang.SuppressWarnings("all")
    public String getCategoryName() {
        return this.categoryName;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setRestaurantId(final UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    @java.lang.SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setDescription(final String description) {
        this.description = description;
    }

    @java.lang.SuppressWarnings("all")
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    @java.lang.SuppressWarnings("all")
    public void setIsAvailable(final Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @java.lang.SuppressWarnings("all")
    public void setPrepTimeMinutes(final Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    @java.lang.SuppressWarnings("all")
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setCategoryId(final UUID categoryId) {
        this.categoryId = categoryId;
    }

    @java.lang.SuppressWarnings("all")
    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof MenuItemDTO)) return false;
        final MenuItemDTO other = (MenuItemDTO) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$isAvailable = this.getIsAvailable();
        final java.lang.Object other$isAvailable = other.getIsAvailable();
        if (this$isAvailable == null ? other$isAvailable != null : !this$isAvailable.equals(other$isAvailable)) return false;
        final java.lang.Object this$prepTimeMinutes = this.getPrepTimeMinutes();
        final java.lang.Object other$prepTimeMinutes = other.getPrepTimeMinutes();
        if (this$prepTimeMinutes == null ? other$prepTimeMinutes != null : !this$prepTimeMinutes.equals(other$prepTimeMinutes)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$restaurantId = this.getRestaurantId();
        final java.lang.Object other$restaurantId = other.getRestaurantId();
        if (this$restaurantId == null ? other$restaurantId != null : !this$restaurantId.equals(other$restaurantId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$description = this.getDescription();
        final java.lang.Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
        final java.lang.Object this$price = this.getPrice();
        final java.lang.Object other$price = other.getPrice();
        if (this$price == null ? other$price != null : !this$price.equals(other$price)) return false;
        final java.lang.Object this$imageUrl = this.getImageUrl();
        final java.lang.Object other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) return false;
        final java.lang.Object this$categoryId = this.getCategoryId();
        final java.lang.Object other$categoryId = other.getCategoryId();
        if (this$categoryId == null ? other$categoryId != null : !this$categoryId.equals(other$categoryId)) return false;
        final java.lang.Object this$categoryName = this.getCategoryName();
        final java.lang.Object other$categoryName = other.getCategoryName();
        if (this$categoryName == null ? other$categoryName != null : !this$categoryName.equals(other$categoryName)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MenuItemDTO;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $isAvailable = this.getIsAvailable();
        result = result * PRIME + ($isAvailable == null ? 43 : $isAvailable.hashCode());
        final java.lang.Object $prepTimeMinutes = this.getPrepTimeMinutes();
        result = result * PRIME + ($prepTimeMinutes == null ? 43 : $prepTimeMinutes.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $restaurantId = this.getRestaurantId();
        result = result * PRIME + ($restaurantId == null ? 43 : $restaurantId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final java.lang.Object $price = this.getPrice();
        result = result * PRIME + ($price == null ? 43 : $price.hashCode());
        final java.lang.Object $imageUrl = this.getImageUrl();
        result = result * PRIME + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        final java.lang.Object $categoryId = this.getCategoryId();
        result = result * PRIME + ($categoryId == null ? 43 : $categoryId.hashCode());
        final java.lang.Object $categoryName = this.getCategoryName();
        result = result * PRIME + ($categoryName == null ? 43 : $categoryName.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "MenuItemDTO(id=" + this.getId() + ", restaurantId=" + this.getRestaurantId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", price=" + this.getPrice() + ", isAvailable=" + this.getIsAvailable() + ", prepTimeMinutes=" + this.getPrepTimeMinutes() + ", imageUrl=" + this.getImageUrl() + ", categoryId=" + this.getCategoryId() + ", categoryName=" + this.getCategoryName() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public MenuItemDTO() {
    }

    @java.lang.SuppressWarnings("all")
    public MenuItemDTO(final UUID id, final UUID restaurantId, final String name, final String description, final BigDecimal price, final Boolean isAvailable, final Integer prepTimeMinutes, final String imageUrl, final UUID categoryId, final String categoryName) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
        this.prepTimeMinutes = prepTimeMinutes;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
