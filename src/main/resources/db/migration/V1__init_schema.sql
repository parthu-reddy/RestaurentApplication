CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE brands (
    id UUID PRIMARY KEY,
    owner_id UUID,
    name VARCHAR(255) NOT NULL,
    gstin VARCHAR(15),
    pan VARCHAR(10),
    cin VARCHAR(21),
    bank_account_number VARCHAR(50),
    bank_ifsc VARCHAR(20),
    is_gstin_verified BOOLEAN DEFAULT FALSE,
    is_bank_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version INTEGER DEFAULT 0,
    logo_url VARCHAR(1024),
    median_price DOUBLE PRECISION DEFAULT 0.0
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    brand_id UUID REFERENCES brands(id)
);

CREATE INDEX idx_categories_brand_id ON categories(brand_id);
CREATE UNIQUE INDEX uq_categories_brand_name ON categories (brand_id, name) WHERE brand_id IS NOT NULL;
CREATE UNIQUE INDEX uq_categories_global_name ON categories (name) WHERE brand_id IS NULL;

CREATE TABLE outlets (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    name VARCHAR(255) NOT NULL,
    fssai_license_number VARCHAR(14),
    location geometry(Point, 4326),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version INTEGER DEFAULT 0,
    banner_url VARCHAR(1024),
    cuisine VARCHAR(255),
    rating DOUBLE PRECISION DEFAULT 0.0,
    reviews_count INTEGER DEFAULT 0,
    delivery_time INTEGER,
    delivery_fee DOUBLE PRECISION,
    tags TEXT,
    default_prep_time_seconds INTEGER DEFAULT 900
);

CREATE TABLE master_menu_items (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
    default_prep_time_minutes INTEGER DEFAULT 15 CHECK (default_prep_time_minutes >= 0),
    version INTEGER DEFAULT 0,
    image_url VARCHAR(1024),
    category_id UUID REFERENCES categories(id)
);

CREATE TABLE outlet_menu_overrides (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL REFERENCES outlets(id),
    master_menu_item_id UUID NOT NULL REFERENCES master_menu_items(id),
    overridden_price DECIMAL(10,2) CHECK (overridden_price >= 0),
    is_available BOOLEAN,
    overridden_prep_time_minutes INTEGER CHECK (overridden_prep_time_minutes >= 0),
    version INTEGER DEFAULT 0
);

CREATE TABLE outlet_timings (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL REFERENCES outlets(id),
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    version INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
CREATE INDEX idx_outlet_timings_outlet_id ON outlet_timings(outlet_id);

CREATE TABLE category_timings (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    version INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
CREATE INDEX idx_category_timings_category_id ON category_timings(category_id);

CREATE TABLE outlet_category_timings (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL REFERENCES outlets(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER
);
CREATE INDEX idx_outlet_category_timings_outlet_id ON outlet_category_timings(outlet_id);
CREATE INDEX idx_outlet_category_timings_category_id ON outlet_category_timings(category_id);

CREATE TABLE brand_category_timings (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);
CREATE INDEX idx_brand_category_timings_brand_id ON brand_category_timings(brand_id);
CREATE INDEX idx_brand_category_timings_category_id ON brand_category_timings(category_id);

CREATE TABLE restaurant_orders (
    order_id UUID PRIMARY KEY,
    restaurant_id UUID,
    status VARCHAR(50),
    prep_time INTEGER CHECK (prep_time >= 0),
    additional_prep_time INTEGER CHECK (additional_prep_time >= 0),
    delivery_otp VARCHAR(255),
    delivery_lat DOUBLE PRECISION CHECK (delivery_lat >= -90 AND delivery_lat <= 90),
    delivery_lng DOUBLE PRECISION CHECK (delivery_lng >= -180 AND delivery_lng <= 180),
    delivery_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    items_json TEXT,
    pickup_otp VARCHAR(255),
    estimated_completion_time BIGINT CHECK (estimated_completion_time >= 0),
    rider_name VARCHAR(255),
    rider_phone VARCHAR(255)
);


