CREATE TABLE outlet_category_timings (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL,
    category_id UUID NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INTEGER,
    CONSTRAINT fk_outlet_category_timings_outlet FOREIGN KEY (outlet_id) REFERENCES outlets (id),
    CONSTRAINT fk_outlet_category_timings_category FOREIGN KEY (category_id) REFERENCES categories (id)
);
