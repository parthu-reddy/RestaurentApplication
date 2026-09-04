ALTER TABLE restaurant_orders ADD COLUMN customer_id UUID;
ALTER TABLE restaurant_orders ADD COLUMN customer_name VARCHAR(255);
ALTER TABLE restaurant_orders ADD COLUMN rider_name VARCHAR(255);
