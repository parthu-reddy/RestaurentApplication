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
