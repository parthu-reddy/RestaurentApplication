ALTER TABLE categories DROP CONSTRAINT categories_name_key;

CREATE UNIQUE INDEX uq_categories_brand_name ON categories (brand_id, name) WHERE brand_id IS NOT NULL;
CREATE UNIQUE INDEX uq_categories_global_name ON categories (name) WHERE brand_id IS NULL;
