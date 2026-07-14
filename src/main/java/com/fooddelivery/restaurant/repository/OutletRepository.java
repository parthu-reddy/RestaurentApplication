package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Outlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface OutletRepository extends org.springframework.data.jpa.repository.JpaRepository<Outlet, UUID> {
    List<Outlet> findByBrandId(UUID brandId);
    List<Outlet> findByBrandIdIn(List<UUID> brandIds);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM outlets o WHERE ST_DWithin(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radiusInMeters) AND o.is_active = true", nativeQuery = true)
    List<Outlet> findNearbyOutlets(@org.springframework.data.repository.query.Param("lat") double lat, @org.springframework.data.repository.query.Param("lng") double lng, @org.springframework.data.repository.query.Param("radiusInMeters") double radiusInMeters);
}
