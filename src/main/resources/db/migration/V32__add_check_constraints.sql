-- V32__add_check_constraints.sql
ALTER TABLE master_menu_items ADD CONSTRAINT chk_master_menu_price CHECK (base_price >= 0);
ALTER TABLE master_menu_items ADD CONSTRAINT chk_master_menu_prep CHECK (default_prep_time_minutes >= 0);

ALTER TABLE outlet_menu_overrides ADD CONSTRAINT chk_outlet_override_price CHECK (overridden_price >= 0);
ALTER TABLE outlet_menu_overrides ADD CONSTRAINT chk_outlet_override_prep CHECK (overridden_prep_time_minutes >= 0);

ALTER TABLE restaurant_orders ADD CONSTRAINT chk_rest_orders_prep CHECK (prep_time >= 0);
ALTER TABLE restaurant_orders ADD CONSTRAINT chk_rest_orders_add_prep CHECK (additional_prep_time >= 0);
ALTER TABLE restaurant_orders ADD CONSTRAINT chk_rest_orders_est_comp CHECK (estimated_completion_time >= 0);
ALTER TABLE restaurant_orders ADD CONSTRAINT chk_rest_orders_del_lat CHECK (delivery_lat >= -90 AND delivery_lat <= 90);
ALTER TABLE restaurant_orders ADD CONSTRAINT chk_rest_orders_del_lng CHECK (delivery_lng >= -180 AND delivery_lng <= 180);
