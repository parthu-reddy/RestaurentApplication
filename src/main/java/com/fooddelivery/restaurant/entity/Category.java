package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
    @jakarta.persistence.Column(name = "brand_id")
    private UUID brandId;
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 500)
    @Column(name = "description")
    private String description;
    @Column(name = "active")
    private Boolean active;
    @OneToMany(mappedBy = "category", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<CategoryTiming> timings;


    @java.lang.SuppressWarnings("all")
    public static class CategoryBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private UUID brandId;
        @java.lang.SuppressWarnings("all")
        private String name;
        @java.lang.SuppressWarnings("all")
        private String description;
        @java.lang.SuppressWarnings("all")
        private Boolean active;
        @java.lang.SuppressWarnings("all")
        private java.util.List<CategoryTiming> timings;

        @java.lang.SuppressWarnings("all")
        CategoryBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Category.CategoryBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Category.CategoryBuilder brandId(final UUID brandId) {
            this.brandId = brandId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Category.CategoryBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Category.CategoryBuilder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Category.CategoryBuilder active(final Boolean active) {
            this.active = active;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Category.CategoryBuilder timings(final java.util.List<CategoryTiming> timings) {
            this.timings = timings;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public Category build() {
            return new Category(this.id, this.brandId, this.name, this.description, this.active, this.timings);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "Category.CategoryBuilder(id=" + this.id + ", brandId=" + this.brandId + ", name=" + this.name + ", description=" + this.description + ", active=" + this.active + ", timings=" + this.timings + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static Category.CategoryBuilder builder() {
        return new Category.CategoryBuilder();
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
    public Boolean getActive() {
        return this.active;
    }

    @java.lang.SuppressWarnings("all")
    public java.util.List<CategoryTiming> getTimings() {
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
    public void setActive(final Boolean active) {
        this.active = active;
    }

    @java.lang.SuppressWarnings("all")
    public void setTimings(final java.util.List<CategoryTiming> timings) {
        this.timings = timings;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Category)) return false;
        final Category other = (Category) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$active = this.getActive();
        final java.lang.Object other$active = other.getActive();
        if (this$active == null ? other$active != null : !this$active.equals(other$active)) return false;
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
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Category;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $active = this.getActive();
        result = result * PRIME + ($active == null ? 43 : $active.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $brandId = this.getBrandId();
        result = result * PRIME + ($brandId == null ? 43 : $brandId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Category(id=" + this.getId() + ", brandId=" + this.getBrandId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", active=" + this.getActive() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public Category() {
    }

    @java.lang.SuppressWarnings("all")
    public Category(final UUID id, final UUID brandId, final String name, final String description, final Boolean active, final java.util.List<CategoryTiming> timings) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.active = active;
        this.timings = timings;
    }
}
