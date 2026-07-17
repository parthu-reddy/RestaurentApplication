ALTER TABLE brand_category_timings
ADD CONSTRAINT fk_brand_category_timings_brand 
FOREIGN KEY (brand_id) REFERENCES brands(id);
