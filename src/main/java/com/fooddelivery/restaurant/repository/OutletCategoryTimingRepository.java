package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.OutletCategoryTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutletCategoryTimingRepository extends JpaRepository<OutletCategoryTiming, UUID> {
    List<OutletCategoryTiming> findByOutletId(UUID outletId);
    List<OutletCategoryTiming> findByOutletIdAndCategoryId(UUID outletId, UUID categoryId);
    void deleteByOutletIdAndCategoryId(UUID outletId, UUID categoryId);
}
