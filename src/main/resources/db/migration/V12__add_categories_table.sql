CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN DEFAULT true
);

INSERT INTO categories (id, name, description, active) VALUES
(gen_random_uuid(), 'Appetizers', 'Starters and small bites', true),
(gen_random_uuid(), 'Main Course', 'Primary dishes', true),
(gen_random_uuid(), 'Desserts', 'Sweet treats', true),
(gen_random_uuid(), 'Beverages', 'Drinks and juices', true);

ALTER TABLE master_menu_items ADD COLUMN category_id UUID REFERENCES categories(id);
