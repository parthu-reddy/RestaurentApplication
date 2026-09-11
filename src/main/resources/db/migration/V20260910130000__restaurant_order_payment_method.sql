-- How the customer paid, on the restaurant's projection of the order.
--
-- ORDER_PAID and ORDER_PLACED_COD are separate events and were consumed by the same handler, which
-- recorded neither the event type nor the method. The restaurant therefore had no way to know an
-- order was cash on delivery -- no "collect Rs.420" on the ticket, and no way for the delivery
-- service downstream to require a declared amount at handover.
ALTER TABLE restaurant_orders ADD COLUMN payment_method VARCHAR(16);
