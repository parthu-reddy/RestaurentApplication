package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Outlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Repository
public interface OutletRepository extends org.springframework.data.jpa.repository.JpaRepository<Outlet, UUID> {
    Page<Outlet> findByCityId(String cityId, Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"timings"})
    List<Outlet> findByBrandId(UUID brandId);
    
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"timings"})
    List<Outlet> findByBrandIdIn(List<UUID> brandIds);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"timings"})
    List<Outlet> findByIdIn(List<UUID> ids);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT o FROM Outlet o, Brand b WHERE o.brandId = b.id AND b.organisationId IN :organisationIds")
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"timings"})
    List<Outlet> findByOrganisationIdIn(@org.springframework.data.repository.query.Param("organisationIds") java.util.Collection<UUID> organisationIds);

    @org.springframework.data.jpa.repository.Query("SELECT new com.fooddelivery.common.dto.restaurant.OutletOrganisationDto(o.id,b.id,b.organisationId) FROM Outlet o, Brand b WHERE o.brandId = b.id AND o.id = :outletId")
    java.util.Optional<com.fooddelivery.common.dto.restaurant.OutletOrganisationDto> findOrganisationByOutletId(@org.springframework.data.repository.query.Param("outletId") UUID outletId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT ON (o.brand_id) o.* FROM outlets o JOIN outlet_timings t ON o.id = t.outlet_id WHERE ST_DWithin(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radiusInMeters) AND o.is_active = true AND ((t.opening_time <= t.closing_time AND (CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time >= t.opening_time AND (CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time <= t.closing_time) OR (t.opening_time > t.closing_time AND ((CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time >= t.opening_time OR (CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time <= t.closing_time))) ORDER BY o.brand_id, ST_Distance(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography)) ASC", nativeQuery = true)
    List<Outlet> findNearbyOutlets(@org.springframework.data.repository.query.Param("lat") double lat, @org.springframework.data.repository.query.Param("lng") double lng, @org.springframework.data.repository.query.Param("radiusInMeters") double radiusInMeters);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT o.* FROM outlets o JOIN outlet_timings t ON o.id = t.outlet_id WHERE o.brand_id = :brandId AND ST_DWithin(CAST(o.location AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radiusInMeters) AND o.is_active = true AND ((t.opening_time <= t.closing_time AND (CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time >= t.opening_time AND (CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time <= t.closing_time) OR (t.opening_time > t.closing_time AND ((CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time >= t.opening_time OR (CURRENT_TIMESTAMP AT TIME ZONE o.time_zone)::time <= t.closing_time)))", nativeQuery = true)
    List<Outlet> findNearbyOutletsByBrand(@org.springframework.data.repository.query.Param("brandId") UUID brandId, @org.springframework.data.repository.query.Param("lat") double lat, @org.springframework.data.repository.query.Param("lng") double lng, @org.springframework.data.repository.query.Param("radiusInMeters") double radiusInMeters);

    @org.springframework.data.jpa.repository.Query(value = "SELECT ST_AsText(location) FROM outlets WHERE id = :id", nativeQuery = true)
    String findLocationWktById(@org.springframework.data.repository.query.Param("id") UUID id);
}
