package com.fooddelivery.restaurant.scheduler;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.FulfillmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestaurantAcceptanceTimeoutPoller {

    private final RestaurantOrderRepository orderRepository;
    private final FulfillmentService fulfillmentService;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Scheduled(fixedDelay = 60000)
    public void pollAcceptanceTimeouts() {
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(com.fooddelivery.common.constants.RedisKeyConstants.LOCK_POLL_ACCEPTANCE_TIMEOUTS, "1", java.time.Duration.ofSeconds(50));
        if (!Boolean.TRUE.equals(locked)) {
            return;
        }

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<RestaurantOrder> unacceptedOrders = orderRepository.findByStatusAndCreatedAtBefore(com.fooddelivery.restaurant.entity.OrderStatus.CREATED, threshold);
        
        if (!unacceptedOrders.isEmpty()) {
            log.info("Found {} unaccepted restaurant orders older than 10 minutes. Auto-rejecting...", unacceptedOrders.size());
            for (RestaurantOrder order : unacceptedOrders) {
                try {
                    fulfillmentService.rejectOrder(order.getRestaurantId(), order.getOrderId(), "Timeout: Order not accepted in time");
                    log.info("Successfully rejected order {} due to timeout", order.getOrderId());
                } catch (Exception e) {
                    log.error("Failed to auto-reject order {}", order.getOrderId(), e);
                }
            }
        }
    }
}
