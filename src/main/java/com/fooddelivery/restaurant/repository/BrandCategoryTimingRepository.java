package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.BrandCategoryTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandCategoryTimingRepository extends JpaRepository<BrandCategoryTiming, UUID> {
    List<BrandCategoryTiming> findByBrandId(UUID brandId);
    List<BrandCategoryTiming> findByBrandIdAndCategoryId(UUID brandId, UUID categoryId);
    void deleteByBrandIdAndCategoryId(UUID brandId, UUID categoryId);
}
