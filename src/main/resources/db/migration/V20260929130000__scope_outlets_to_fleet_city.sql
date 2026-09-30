-- An outlet's point and time zone are not a stable fleet operating-area identifier. Existing
-- records predate multiple city support and are therefore backfilled to the documented deployment
-- default (BLR), never inferred from arbitrary coordinates or display text.
ALTER TABLE outlets
    ADD COLUMN IF NOT EXISTS city_id VARCHAR(64);

UPDATE outlets
SET city_id = 'BLR'
WHERE city_id IS NULL;

ALTER TABLE outlets
    ALTER COLUMN city_id SET NOT NULL;

ALTER TABLE outlets
    ADD CONSTRAINT ck_outlets_city_id_canonical
    CHECK (city_id ~ '^[A-Z][A-Z0-9_-]{0,63}$');

CREATE INDEX IF NOT EXISTS idx_outlets_city_id
    ON outlets (city_id, id);
