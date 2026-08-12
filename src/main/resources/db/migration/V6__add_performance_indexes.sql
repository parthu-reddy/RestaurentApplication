-- Performance optimization indexes for RestaurantApplication
-- Addresses table scans identified during architectural audit for dashboards, outbox polling, and FK lookups

-- Indexes for restaurant orders (critical for dashboard queries, filtering by status, time ranges, and driver assignments)
CREATE INDEX IF NOT EXISTS idx_restaurant_orders_restaurant_status_created ON restaurant_orders(restaurant_id, status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_restaurant_orders_status_created ON restaurant_orders(status, created_at);
CREATE INDEX IF NOT EXISTS idx_restaurant_orders_delivery_exec_id ON restaurant_orders(delivery_executive_id) WHERE delivery_executive_id IS NOT NULL;


-- Indexes on foreign keys and frequently queried columns to prevent sequential table scans
CREATE INDEX IF NOT EXISTS idx_brands_owner_id ON brands(owner_id) WHERE owner_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_outlets_brand_id ON outlets(brand_id);
CREATE INDEX IF NOT EXISTS idx_master_menu_items_brand_id ON master_menu_items(brand_id);
CREATE INDEX IF NOT EXISTS idx_outlet_menu_overrides_outlet_item ON outlet_menu_overrides(outlet_id, master_menu_item_id);
