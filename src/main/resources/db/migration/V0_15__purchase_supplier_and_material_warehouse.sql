ALTER TABLE purchase
    DROP COLUMN IF EXISTS supplier,
    ADD COLUMN IF NOT EXISTS supplier_id VARCHAR(150) REFERENCES "users"(id);

ALTER TABLE purchase
    ALTER COLUMN equipment DROP NOT NULL,
    ALTER COLUMN material DROP NOT NULL;

CREATE TABLE IF NOT EXISTS material_warehouse (
    material_id VARCHAR(150) NOT NULL REFERENCES material(id),
    warehouse_id VARCHAR(150) NOT NULL REFERENCES warehouse(id),
    quantity INTEGER NOT NULL,
    CONSTRAINT material_warehouse_pk PRIMARY KEY (material_id, warehouse_id)
);
