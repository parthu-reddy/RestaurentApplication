UPDATE master_menu_items 
SET category_id = (SELECT id FROM categories WHERE name = 'Food' LIMIT 1) 
WHERE category_id IS NULL;
