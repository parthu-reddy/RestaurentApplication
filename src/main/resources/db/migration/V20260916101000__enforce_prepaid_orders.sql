DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM restaurant_orders
        WHERE payment_method IS NULL OR payment_method NOT IN ('CARD', 'UPI', 'WALLET')
    ) OR EXISTS (
        SELECT 1 FROM restaurant_orders
        WHERE payment_status IN ('PENDING_COLLECTION', 'COLLECTED')
    ) THEN
        RAISE EXCEPTION 'Prepaid-only migration blocked: unsupported restaurant order rows exist';
    END IF;
END $$;

ALTER TABLE restaurant_orders
    ALTER COLUMN payment_method SET NOT NULL,
    ADD CONSTRAINT chk_restaurant_order_payment_method_prepaid
    CHECK (payment_method IN ('CARD', 'UPI', 'WALLET'));

ALTER TABLE restaurant_orders
    ADD CONSTRAINT chk_restaurant_order_payment_status_prepaid
    CHECK (payment_status NOT IN ('PENDING_COLLECTION', 'COLLECTED'));
