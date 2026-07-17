CREATE TABLE brand_category_timings (
    id UUID PRIMARY KEY,
    brand_id UUID NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT uq_brand_category UNIQUE (brand_id, category_id)
);
