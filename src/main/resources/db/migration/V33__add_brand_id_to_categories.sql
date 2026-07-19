ALTER TABLE categories ADD COLUMN brand_id UUID;
CREATE INDEX idx_categories_brand_id ON categories(brand_id);
