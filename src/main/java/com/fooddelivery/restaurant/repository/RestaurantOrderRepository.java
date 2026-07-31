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
    @org.springframework.data.jpa.repository.Query(
        "SELECT o FROM RestaurantOrder o WHERE o.restaurantId = :restaurantId " +
        "AND o.status NOT IN :cancelledStatuses " +
        "AND (o.deliveryStatus IS NULL OR o.deliveryStatus NOT IN :terminalDeliveryStatuses)"
    )
    List<RestaurantOrder> findActiveOrdersByRestaurantId(
        @org.springframework.data.repository.query.Param("restaurantId") UUID restaurantId,
        @org.springframework.data.repository.query.Param("cancelledStatuses") List<OrderStatus> cancelledStatuses,
        @org.springframework.data.repository.query.Param("terminalDeliveryStatuses") List<com.fooddelivery.common.enums.DeliveryStatus> terminalDeliveryStatuses
    );

    @org.springframework.data.jpa.repository.Query(
        "SELECT o FROM RestaurantOrder o WHERE o.restaurantId = :restaurantId " +
        "AND (o.status IN :cancelledStatuses OR o.deliveryStatus IN :terminalDeliveryStatuses) " +
        "AND (cast(:startDate as timestamp) IS NULL OR o.createdAt >= :startDate) " +
        "AND (cast(:endDate as timestamp) IS NULL OR o.createdAt <= :endDate)"
    )
    org.springframework.data.domain.Page<RestaurantOrder> findHistoryOrdersByRestaurantId(
        @org.springframework.data.repository.query.Param("restaurantId") UUID restaurantId, 
        @org.springframework.data.repository.query.Param("cancelledStatuses") List<OrderStatus> cancelledStatuses,
        @org.springframework.data.repository.query.Param("terminalDeliveryStatuses") List<com.fooddelivery.common.enums.DeliveryStatus> terminalDeliveryStatuses,
        @org.springframework.data.repository.query.Param("startDate") LocalDateTime startDate, 
        @org.springframework.data.repository.query.Param("endDate") LocalDateTime endDate, 
        org.springframework.data.domain.Pageable pageable
    );
}
