ALTER TABLE categories
ADD CONSTRAINT fk_categories_brand_id
FOREIGN KEY (brand_id) REFERENCES brands(id);
