ALTER TABLE restaurant_orders
DROP COLUMN rider_name,
DROP COLUMN rider_phone,
ADD COLUMN delivery_executive_id UUID;
