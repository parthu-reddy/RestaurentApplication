-- Source: V1__init_schema.sql
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
    updated_at TIMESTAMP
);

CREATE TABLE outlets (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    name VARCHAR(255) NOT NULL,
    fssai_license_number VARCHAR(14),
    location geometry(Point, 4326),
    opening_time TIME,
    closing_time TIME,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE master_menu_items (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL REFERENCES brands(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2) NOT NULL,
    default_prep_time_minutes INTEGER DEFAULT 15
);

CREATE TABLE outlet_menu_overrides (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL REFERENCES outlets(id),
    master_menu_item_id UUID NOT NULL REFERENCES master_menu_items(id),
    overridden_price DECIMAL(10,2),
    is_available BOOLEAN,
    overridden_prep_time_minutes INTEGER
);


-- Source: V2__add_outbox.sql
CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPROCESSED',
    processed_at TIMESTAMP,
    error_message VARCHAR(1000)
);


-- Source: V3__add_restaurant_orders.sql
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


-- Source: V4__add_timestamps.sql
ALTER TABLE restaurant_orders
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


-- Source: V5__add_version_column.sql
ALTER TABLE restaurant_orders ADD COLUMN version INTEGER DEFAULT 0;
ALTER TABLE brands ADD COLUMN version INTEGER DEFAULT 0;
ALTER TABLE outlets ADD COLUMN version INTEGER DEFAULT 0;
ALTER TABLE master_menu_items ADD COLUMN version INTEGER DEFAULT 0;
ALTER TABLE outlet_menu_overrides ADD COLUMN version INTEGER DEFAULT 0;


-- Source: V10__add_retry_count_to_outbox.sql
ALTER TABLE outbox_events ADD COLUMN IF NOT EXISTS retry_count INT DEFAULT 0;


