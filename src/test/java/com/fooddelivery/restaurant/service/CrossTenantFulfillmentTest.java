package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant.client.DeliveryClient;
import com.fooddelivery.restaurant.client.OrderClient;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * An outlet owner may only drive their own orders.
 *
 * <p>`FulfillmentController`'s `@PreAuthorize` proves the caller owns `{restaurantId}` and nothing
 * about `{orderId}`. Five of the six mutators then loaded the order by id alone and never compared
 * the two, so any outlet owner could accept, reject, prepare, ready or cancel another restaurant's
 * order — the cancel path issuing a real refund and a RESTAURANT_FAULT clawback against the victim.
 *
 * <p>The binding now lives in the query, so these tests exercise it through the service exactly as
 * the controller does.
 */
class CrossTenantFulfillmentTest {

    private RestaurantOrderRepository orderRepository;
    private RestaurantActionService actionService;
    private OutletRepository outletRepository;
    private FulfillmentService service;

    private final UUID attackerOutlet = UUID.randomUUID();
    private final UUID victimOutlet = UUID.randomUUID();
    private final UUID victimOrderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        orderRepository = mock(RestaurantOrderRepository.class);
        actionService = mock(RestaurantActionService.class);
        outletRepository = mock(OutletRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();

        lenient().when(outletRepository.findById(any())).thenReturn(Optional.of(new Outlet()));
        lenient().when(actionService.getRestaurantCoordinates(any())).thenReturn(new double[]{12.9, 77.6});
        lenient().when(actionService.createPayloadNode()).thenAnswer(i -> objectMapper.createObjectNode());

        service = new FulfillmentService(outletRepository, orderRepository, actionService,
                mock(DeliveryClient.class), mock(OrderClient.class));
    }

    /** The victim's order exists, but not for the attacker's outlet. */
    private RestaurantOrder victimOrder(OrderStatus status) {
        RestaurantOrder order = RestaurantOrder.builder()
                .orderId(victimOrderId)
                .restaurantId(victimOutlet)
                .status(status)
                .prepTime(20)
                .additionalPrepTime(0)
                .build();
        when(orderRepository.findByOrderIdAndRestaurantId(victimOrderId, victimOutlet))
                .thenReturn(Optional.of(order));
        when(orderRepository.findByOrderIdAndRestaurantId(victimOrderId, attackerOutlet))
                .thenReturn(Optional.empty());
        // The unsafe load, stubbed to behave the way it used to: the order comes back for anyone
        // who names its id. Without this the tests would pass under a revert for the wrong reason --
        // findById would simply return empty and the *owner* test would be the one that failed.
        // With it, reverting the binding makes the cross-tenant tests fail, which is the assertion.
        lenient().when(orderRepository.findById(victimOrderId)).thenReturn(Optional.of(order));
        return order;
    }

    @Test
    void anotherRestaurantCannotAcceptTheOrder() {
        RestaurantOrder order = victimOrder(OrderStatus.CREATED);

        assertThrows(ResourceNotFoundException.class,
                () -> service.acceptOrder(attackerOutlet, victimOrderId, null, null));

        assertEquals(OrderStatus.CREATED, order.getStatus(), "the victim's order must not move");
        verify(actionService, never()).publishEvent(any(), any(), any());
        verify(actionService, never()).saveOrder(any());
    }

    @Test
    void anotherRestaurantCannotCancelTheOrder() {
        RestaurantOrder order = victimOrder(OrderStatus.ACCEPTED);

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelOrderAfterAccept(attackerOutlet, victimOrderId, "not mine"));

        assertEquals(OrderStatus.ACCEPTED, order.getStatus());
        verify(actionService, never()).publishEvent(any(), any(), any());
    }

    @Test
    void everyMutatorRefusesAnotherRestaurantsOrder() {
        victimOrder(OrderStatus.ACCEPTED);

        List<Runnable> mutators = List.of(
                () -> service.acceptOrder(attackerOutlet, victimOrderId, null, null),
                () -> service.prepareOrder(attackerOutlet, victimOrderId),
                () -> service.rejectOrder(attackerOutlet, victimOrderId, "x"),
                () -> service.readyOrder(attackerOutlet, victimOrderId),
                () -> service.cancelOrderAfterAccept(attackerOutlet, victimOrderId, "x"),
                () -> service.initiatePartialRefund(attackerOutlet, victimOrderId,
                        new BigDecimal("10.00"), List.of(), "x"));

        for (Runnable mutator : mutators) {
            assertThrows(ResourceNotFoundException.class, mutator::run);
        }
        verify(actionService, never()).publishEvent(any(), any(), any());
        verify(actionService, never()).saveOrder(any());
    }

    /** A missing order is a 404, not a 200 saying the action succeeded. */
    @Test
    void aMissingOrderIsNotSilentlyIgnored() {
        UUID unknown = UUID.randomUUID();
        when(orderRepository.findByOrderIdAndRestaurantId(eq(unknown), any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.prepareOrder(attackerOutlet, unknown));
        assertThrows(ResourceNotFoundException.class, () -> service.readyOrder(attackerOutlet, unknown));
        assertThrows(ResourceNotFoundException.class, () -> service.rejectOrder(attackerOutlet, unknown, "x"));
        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelOrderAfterAccept(attackerOutlet, unknown, "x"));
    }

    /** The owner still works — a guard that denies everyone is not a fix. */
    @Test
    void theOwningRestaurantCanStillAcceptItsOwnOrder() {
        RestaurantOrder order = victimOrder(OrderStatus.CREATED);

        service.acceptOrder(victimOutlet, victimOrderId, null, null);

        assertEquals(OrderStatus.ACCEPTED, order.getStatus());
        verify(actionService).publishEvent(eq(victimOrderId.toString()),
                eq(com.fooddelivery.common.constants.EventType.ORDER_ACCEPTED), any());
    }
}
