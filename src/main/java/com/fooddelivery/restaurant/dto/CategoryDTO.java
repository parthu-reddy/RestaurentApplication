package com.fooddelivery.restaurant.dto;

import java.util.UUID;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID brandId;
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    private java.util.List<CategoryTimingDTO> timings;


    public static class CategoryTimingDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private java.time.LocalTime openingTime;
        private java.time.LocalTime closingTime;


        @java.lang.SuppressWarnings("all")
        public static class CategoryTimingDTOBuilder {
            @java.lang.SuppressWarnings("all")
            private java.time.LocalTime openingTime;
            @java.lang.SuppressWarnings("all")
            private java.time.LocalTime closingTime;

            @java.lang.SuppressWarnings("all")
            CategoryTimingDTOBuilder() {
            }

            /**
             * @return {@code this}.
             */
            @java.lang.SuppressWarnings("all")
            public CategoryDTO.CategoryTimingDTO.CategoryTimingDTOBuilder openingTime(final java.time.LocalTime openingTime) {
                this.openingTime = openingTime;
                return this;
            }

            /**
             * @return {@code this}.
             */
            @java.lang.SuppressWarnings("all")
            public CategoryDTO.CategoryTimingDTO.CategoryTimingDTOBuilder closingTime(final java.time.LocalTime closingTime) {
                this.closingTime = closingTime;
                return this;
            }

            @java.lang.SuppressWarnings("all")
            public CategoryDTO.CategoryTimingDTO build() {
                return new CategoryDTO.CategoryTimingDTO(this.openingTime, this.closingTime);
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            public java.lang.String toString() {
                return "CategoryDTO.CategoryTimingDTO.CategoryTimingDTOBuilder(openingTime=" + this.openingTime + ", closingTime=" + this.closingTime + ")";
            }
        }

        @java.lang.SuppressWarnings("all")
        public static CategoryDTO.CategoryTimingDTO.CategoryTimingDTOBuilder builder() {
            return new CategoryDTO.CategoryTimingDTO.CategoryTimingDTOBuilder();
        }

        @java.lang.SuppressWarnings("all")
        public java.time.LocalTime getOpeningTime() {
            return this.openingTime;
        }

        @java.lang.SuppressWarnings("all")
        public java.time.LocalTime getClosingTime() {
            return this.closingTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setOpeningTime(final java.time.LocalTime openingTime) {
            this.openingTime = openingTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setClosingTime(final java.time.LocalTime closingTime) {
            this.closingTime = closingTime;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof CategoryDTO.CategoryTimingDTO)) return false;
            final CategoryDTO.CategoryTimingDTO other = (CategoryDTO.CategoryTimingDTO) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$openingTime = this.getOpeningTime();
            final java.lang.Object other$openingTime = other.getOpeningTime();
            if (this$openingTime == null ? other$openingTime != null : !this$openingTime.equals(other$openingTime)) return false;
            final java.lang.Object this$closingTime = this.getClosingTime();
            final java.lang.Object other$closingTime = other.getClosingTime();
            if (this$closingTime == null ? other$closingTime != null : !this$closingTime.equals(other$closingTime)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof CategoryDTO.CategoryTimingDTO;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $openingTime = this.getOpeningTime();
            result = result * PRIME + ($openingTime == null ? 43 : $openingTime.hashCode());
            final java.lang.Object $closingTime = this.getClosingTime();
            result = result * PRIME + ($closingTime == null ? 43 : $closingTime.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "CategoryDTO.CategoryTimingDTO(openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ")";
        }

        @java.lang.SuppressWarnings("all")
        public CategoryTimingDTO() {
        }

        @java.lang.SuppressWarnings("all")
        public CategoryTimingDTO(final java.time.LocalTime openingTime, final java.time.LocalTime closingTime) {
            this.openingTime = openingTime;
            this.closingTime = closingTime;
        }
    }


    @java.lang.SuppressWarnings("all")
    public static class CategoryDTOBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private UUID brandId;
        @java.lang.SuppressWarnings("all")
        private String name;
        @java.lang.SuppressWarnings("all")
        private String description;
        @java.lang.SuppressWarnings("all")
        private java.util.List<CategoryTimingDTO> timings;

        @java.lang.SuppressWarnings("all")
        CategoryDTOBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryDTO.CategoryDTOBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryDTO.CategoryDTOBuilder brandId(final UUID brandId) {
            this.brandId = brandId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryDTO.CategoryDTOBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryDTO.CategoryDTOBuilder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public CategoryDTO.CategoryDTOBuilder timings(final java.util.List<CategoryTimingDTO> timings) {
            this.timings = timings;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public CategoryDTO build() {
            return new CategoryDTO(this.id, this.brandId, this.name, this.description, this.timings);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "CategoryDTO.CategoryDTOBuilder(id=" + this.id + ", brandId=" + this.brandId + ", name=" + this.name + ", description=" + this.description + ", timings=" + this.timings + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static CategoryDTO.CategoryDTOBuilder builder() {
        return new CategoryDTO.CategoryDTOBuilder();
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
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public String getDescription() {
        return this.description;
    }

    @java.lang.SuppressWarnings("all")
    public java.util.List<CategoryTimingDTO> getTimings() {
        return this.timings;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setBrandId(final UUID brandId) {
        this.brandId = brandId;
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
    public void setTimings(final java.util.List<CategoryTimingDTO> timings) {
        this.timings = timings;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof CategoryDTO)) return false;
        final CategoryDTO other = (CategoryDTO) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$brandId = this.getBrandId();
        final java.lang.Object other$brandId = other.getBrandId();
        if (this$brandId == null ? other$brandId != null : !this$brandId.equals(other$brandId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$description = this.getDescription();
        final java.lang.Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
        final java.lang.Object this$timings = this.getTimings();
        final java.lang.Object other$timings = other.getTimings();
        if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CategoryDTO;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $brandId = this.getBrandId();
        result = result * PRIME + ($brandId == null ? 43 : $brandId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final java.lang.Object $timings = this.getTimings();
        result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "CategoryDTO(id=" + this.getId() + ", brandId=" + this.getBrandId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", timings=" + this.getTimings() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public CategoryDTO() {
    }

    @java.lang.SuppressWarnings("all")
    public CategoryDTO(final UUID id, final UUID brandId, final String name, final String description, final java.util.List<CategoryTimingDTO> timings) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.timings = timings;
    }
}
