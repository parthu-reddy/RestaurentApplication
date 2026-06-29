CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE restaurants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    fssai_license_number VARCHAR(100),
    gstin VARCHAR(100),
    pan VARCHAR(10),
    cin VARCHAR(21),
    is_active BOOLEAN DEFAULT false,
    location geometry(Point, 4326),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_restaurant_location ON restaurants USING GIST(location);

CREATE TABLE menu_items (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_menu_items_restaurant_id ON menu_items(restaurant_id);


