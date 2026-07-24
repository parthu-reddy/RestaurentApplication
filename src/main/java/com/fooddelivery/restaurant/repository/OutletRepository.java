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

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT ON (o.brand_id) o.* FROM outlets o JOIN outlet_timings t ON o.id = t.outlet_id WHERE ST_DWithin(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radiusInMeters) AND o.is_active = true AND ((t.opening_time <= t.closing_time AND (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time >= t.opening_time AND (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time <= t.closing_time) OR (t.opening_time > t.closing_time AND ((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time >= t.opening_time OR (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time <= t.closing_time))) ORDER BY o.brand_id, ST_Distance(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography)) ASC", nativeQuery = true)
    List<Outlet> findNearbyOutlets(@org.springframework.data.repository.query.Param("lat") double lat, @org.springframework.data.repository.query.Param("lng") double lng, @org.springframework.data.repository.query.Param("radiusInMeters") double radiusInMeters);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT o.* FROM outlets o JOIN outlet_timings t ON o.id = t.outlet_id WHERE o.brand_id = :brandId AND ST_DWithin(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radiusInMeters) AND o.is_active = true AND ((t.opening_time <= t.closing_time AND (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time >= t.opening_time AND (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time <= t.closing_time) OR (t.opening_time > t.closing_time AND ((CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time >= t.opening_time OR (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')::time <= t.closing_time)))", nativeQuery = true)
    List<Outlet> findNearbyOutletsByBrand(@org.springframework.data.repository.query.Param("brandId") UUID brandId, @org.springframework.data.repository.query.Param("lat") double lat, @org.springframework.data.repository.query.Param("lng") double lng, @org.springframework.data.repository.query.Param("radiusInMeters") double radiusInMeters);
}
