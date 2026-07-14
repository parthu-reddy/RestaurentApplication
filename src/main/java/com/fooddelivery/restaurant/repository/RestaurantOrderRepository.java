package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, UUID> {
    List<RestaurantOrder> findByStatusAndCreatedAtBefore(String status, LocalDateTime time);
    List<RestaurantOrder> findByRestaurantId(UUID restaurantId);
}
