ALTER TABLE restaurant_orders ADD COLUMN restaurant_platform_fee DECIMAL(10,2);
ALTER TABLE restaurant_orders ADD COLUMN restaurant_delivery_contribution DECIMAL(10,2);
ALTER TABLE restaurant_orders ADD COLUMN platform_bonus DECIMAL(10,2);
ALTER TABLE restaurant_orders ADD COLUMN restaurant_payout DECIMAL(10,2);
