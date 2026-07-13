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

-- Migrate existing timings
INSERT INTO outlet_timings (id, outlet_id, opening_time, closing_time, created_at, updated_at)
SELECT gen_random_uuid(), id, opening_time, closing_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM outlets
WHERE opening_time IS NOT NULL AND closing_time IS NOT NULL;

ALTER TABLE outlets DROP COLUMN opening_time;
ALTER TABLE outlets DROP COLUMN closing_time;
