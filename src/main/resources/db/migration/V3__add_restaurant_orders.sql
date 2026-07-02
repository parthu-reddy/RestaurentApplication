CREATE TABLE restaurant_orders (
    order_id UUID PRIMARY KEY,
    restaurant_id UUID,
    status VARCHAR(50),
    prep_time INTEGER,
    additional_prep_time INTEGER,
    delivery_lat DOUBLE PRECISION,
    delivery_lng DOUBLE PRECISION,
    delivery_address VARCHAR(255)
);
