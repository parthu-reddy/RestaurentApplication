-- Insert an override for every outlet and master_menu_item pair that exists, if it doesn't already exist
INSERT INTO outlet_menu_overrides (id, outlet_id, master_menu_item_id, is_available)
SELECT gen_random_uuid(), o.id, m.id, true
FROM outlets o
JOIN master_menu_items m ON o.brand_id = m.brand_id
LEFT JOIN outlet_menu_overrides omo ON omo.outlet_id = o.id AND omo.master_menu_item_id = m.id
WHERE omo.id IS NULL;
