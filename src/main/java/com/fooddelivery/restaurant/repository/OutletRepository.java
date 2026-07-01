package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Outlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutletRepository extends JpaRepository<Outlet, UUID> {
    List<Outlet> findByBrandId(UUID brandId);
}
