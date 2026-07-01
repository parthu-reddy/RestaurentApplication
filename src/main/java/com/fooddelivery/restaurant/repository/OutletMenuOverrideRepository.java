package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.OutletMenuOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutletMenuOverrideRepository extends JpaRepository<OutletMenuOverride, UUID> {
    List<OutletMenuOverride> findByOutletId(UUID outletId);
    Optional<OutletMenuOverride> findByOutletIdAndMasterMenuItemId(UUID outletId, UUID masterMenuItemId);
}
