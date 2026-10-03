CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE brands (
    id UUID PRIMARY KEY,
    organisation_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    gstin VARCHAR(15),
    pan VARCHAR(10),
    cin VARCHAR(21),
    bank_account_number VARCHAR(50),
    bank_ifsc VARCHAR(20),
    is_gstin_verified BOOLEAN DEFAULT FALSE,
    is_bank_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    version INTEGER DEFAULT 0,
    logo_url VARCHAR(1024),
    median_price DOUBLE PRECISION DEFAULT 0.0,
    legal_entity_name VARCHAR(255),
    kyc_status VARCHAR(50) DEFAULT 'PENDING',
    bank_beneficiary_name VARCHAR(255),
    penny_drop_status VARCHAR(50) DEFAULT 'PENDING',
    CONSTRAINT uq_brands_pan UNIQUE (pan),
    CONSTRAINT uq_brands_gstin  UNIQUE (gstin)
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    brand_id UUID REFERENCES brands(id)
);

CREATE TABLE outlets (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    name VARCHAR(255) NOT NULL,
    time_zone VARCHAR(64) NOT NULL,
    city_id VARCHAR(64) NOT NULL CONSTRAINT ck_outlets_city_id_canonical CHECK (city_id ~ '^[A-Z][A-Z0-9_-]{0,63}$'),
    fssai_license_number VARCHAR(14),
    location geometry(Point, 4326),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    version INTEGER DEFAULT 0,
    banner_url VARCHAR(1024),
    cuisine VARCHAR(255),
    rating DOUBLE PRECISION DEFAULT 0.0,
    reviews_count INTEGER DEFAULT 0,
    delivery_time INTEGER,
    delivery_fee DECIMAL(15,2),
    tags TEXT,
    default_prep_time_seconds INTEGER DEFAULT 900,
    CONSTRAINT uq_outlets_fssai UNIQUE (fssai_license_number)
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
    category_id UUID REFERENCES categories(id),
    packing_charge DECIMAL(10,2) DEFAULT 0.00 NOT NULL,
    is_veg BOOLEAN DEFAULT FALSE
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
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE category_timings (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    version INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE outlet_category_timings (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL REFERENCES outlets(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    version INTEGER
);

CREATE TABLE brand_category_timings (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

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
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    items_json TEXT,
    pickup_otp VARCHAR(255),
    estimated_completion_time BIGINT CHECK (estimated_completion_time >= 0),
    rider_name VARCHAR(255),
    delivery_status VARCHAR(50),
    payment_status VARCHAR(50) CONSTRAINT chk_restaurant_order_payment_status_prepaid CHECK (payment_status NOT IN ('PENDING_COLLECTION', 'COLLECTED')),
    customer_name VARCHAR(255),
    delivery_executive_id UUID,
    food_cost DECIMAL(10,2),
    restaurant_platform_fee DECIMAL(10,2),
    restaurant_delivery_contribution DECIMAL(10,2),
    platform_bonus DECIMAL(10,2),
    restaurant_payout DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    customer_id UUID,
    payment_method VARCHAR(16) NOT NULL CONSTRAINT chk_restaurant_order_payment_method_prepaid CHECK (payment_method IN ('CARD', 'UPI', 'WALLET')),
    dispatch_city_id VARCHAR(64) NOT NULL,
    fleet_search_radius_km DOUBLE PRECISION NOT NULL
);



CREATE UNIQUE INDEX uq_categories_brand_name ON categories (brand_id, name) WHERE brand_id IS NOT NULL;

CREATE UNIQUE INDEX uq_categories_global_name ON categories (name) WHERE brand_id IS NULL;





























CREATE INDEX idx_categories_brand_id ON categories(brand_id);

CREATE INDEX idx_outlet_timings_outlet_id ON outlet_timings(outlet_id);

CREATE INDEX idx_category_timings_category_id ON category_timings(category_id);

CREATE INDEX idx_outlet_category_timings_outlet_id ON outlet_category_timings(outlet_id);

CREATE INDEX idx_outlet_category_timings_category_id ON outlet_category_timings(category_id);

CREATE INDEX idx_brand_category_timings_brand_id ON brand_category_timings(brand_id);

CREATE INDEX idx_brand_category_timings_category_id ON brand_category_timings(category_id);

CREATE INDEX IF NOT EXISTS idx_outlets_location_gist ON outlets USING GIST (location);

CREATE INDEX IF NOT EXISTS idx_restaurant_orders_status_created ON restaurant_orders(status, created_at);

CREATE INDEX IF NOT EXISTS idx_restaurant_orders_delivery_exec_id ON restaurant_orders(delivery_executive_id) WHERE delivery_executive_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_outlets_brand_id ON outlets(brand_id);

CREATE INDEX IF NOT EXISTS idx_master_menu_items_brand_id ON master_menu_items(brand_id);

CREATE INDEX IF NOT EXISTS idx_outlet_menu_overrides_outlet_item ON outlet_menu_overrides(outlet_id, master_menu_item_id);

CREATE INDEX IF NOT EXISTS idx_restaurant_orders_restaurant_status_created ON restaurant_orders(restaurant_id, status, created_at DESC);

CREATE UNIQUE INDEX uq_brands_organisation ON brands (organisation_id);
CREATE INDEX idx_outlets_city_id ON outlets (city_id, id);
