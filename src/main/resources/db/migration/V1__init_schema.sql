CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE brands (
    id UUID PRIMARY KEY,
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
