CREATE TABLE IF NOT EXISTS material_warehouse (
    material_id VARCHAR(150) NOT NULL REFERENCES material(id),
    warehouse_id VARCHAR(150) NOT NULL REFERENCES warehouse(id),
    quantity INTEGER NOT NULL,
    CONSTRAINT material_warehouse_pk PRIMARY KEY (material_id, warehouse_id)
);
