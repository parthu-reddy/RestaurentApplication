package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"timings"})
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Category c WHERE c.active = true AND (c.brandId IS NULL OR c.brandId = :brandId)")
    List<Category> findActiveCategoriesForBrand(@org.springframework.data.repository.query.Param("brandId") UUID brandId);
    
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"timings"})
    List<Category> findByActiveTrueAndBrandIdIsNull();
    
    java.util.Optional<Category> findByName(String name);
    java.util.Optional<Category> findByNameAndBrandId(String name, UUID brandId);
}
