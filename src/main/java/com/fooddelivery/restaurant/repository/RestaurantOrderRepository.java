package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fooddelivery.restaurant.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, UUID> {
    List<RestaurantOrder> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime time);
    List<RestaurantOrder> findByRestaurantId(UUID restaurantId);
    List<RestaurantOrder> findByRestaurantIdAndStatusIn(UUID restaurantId, List<OrderStatus> statuses);

    org.springframework.data.domain.Page<RestaurantOrder> findByRestaurantIdAndStatusNotInAndCreatedAtBetween(
        UUID restaurantId, 
        List<OrderStatus> statuses, 
        LocalDateTime start, 
        LocalDateTime end, 
        org.springframework.data.domain.Pageable pageable
    );

    org.springframework.data.domain.Page<RestaurantOrder> findByRestaurantIdAndStatusNotIn(
        UUID restaurantId, 
        List<OrderStatus> statuses, 
        org.springframework.data.domain.Pageable pageable
    );
}
