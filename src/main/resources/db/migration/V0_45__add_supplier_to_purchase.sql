ALTER TABLE purchase RENAME COLUMN supplier_id TO source_warehouse_id;
ALTER TABLE purchase ADD COLUMN IF NOT EXISTS supplier_id VARCHAR(150) REFERENCES supplier(id);
CREATE INDEX IF NOT EXISTS idx_purchase_supplier ON purchase(supplier_id);
