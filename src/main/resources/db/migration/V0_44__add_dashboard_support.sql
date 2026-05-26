-- ============================================================
-- New Fields on Existing Entities
-- ============================================================

-- Material: add expiry date
ALTER TABLE material
ADD COLUMN IF NOT EXISTS expiry_date DATE;

-- Equipment: add leasing and document fields
ALTER TABLE equipment
ADD COLUMN IF NOT EXISTS is_leased BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS lease_end_date DATE,
ADD COLUMN IF NOT EXISTS document_url VARCHAR(500);

-- Job: add expected sale price (plan de vente)
ALTER TABLE job
ADD COLUMN IF NOT EXISTS expected_price NUMERIC(19,2);

-- Loan: add status column (previously missing)
ALTER TABLE loan
ADD COLUMN IF NOT EXISTS status VARCHAR(50);

-- ============================================================
-- New Table: Supplier (Fournisseur)
-- ============================================================
CREATE TABLE IF NOT EXISTS supplier (
    id VARCHAR(150) CONSTRAINT supplier_pk PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    siret VARCHAR(14),
    address TEXT,
    email VARCHAR(255),
    phone VARCHAR(50),
    contact_name VARCHAR(255),
    company_id VARCHAR(150) NOT NULL CONSTRAINT supplier_company_fk REFERENCES company(id)
);
SELECT add_audit_columns('supplier');

-- ============================================================
-- New Table: Purchase Order (Commande Fournisseur)
-- ============================================================
CREATE TABLE IF NOT EXISTS purchase_order (
    id VARCHAR(150) CONSTRAINT purchase_order_pk PRIMARY KEY DEFAULT uuid_generate_v4(),
    supplier_id VARCHAR(150) NOT NULL CONSTRAINT po_supplier_fk REFERENCES supplier(id),
    order_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_amount NUMERIC(19,2),
    company_id VARCHAR(150) NOT NULL CONSTRAINT po_company_fk REFERENCES company(id),
    job_id VARCHAR(150) CONSTRAINT po_job_fk REFERENCES job(id)
);
SELECT add_audit_columns('purchase_order');

CREATE TABLE IF NOT EXISTS purchase_order_line (
    id VARCHAR(150) CONSTRAINT purchase_order_line_pk PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_order_id VARCHAR(150) NOT NULL CONSTRAINT pol_po_fk REFERENCES purchase_order(id),
    material_id VARCHAR(150) CONSTRAINT pol_material_fk REFERENCES material(id),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(19,2)
);
SELECT add_audit_columns('purchase_order_line');

-- ============================================================
-- New Table: Maintenance Schedule (Planification Maintenance)
-- ============================================================
CREATE TABLE IF NOT EXISTS maintenance_schedule (
    id VARCHAR(150) CONSTRAINT maintenance_schedule_pk PRIMARY KEY DEFAULT uuid_generate_v4(),
    equipment_id VARCHAR(150) NOT NULL CONSTRAINT ms_equipment_fk REFERENCES equipment(id),
    description TEXT,
    scheduled_date DATE NOT NULL,
    frequency VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    company_id VARCHAR(150) NOT NULL CONSTRAINT ms_company_fk REFERENCES company(id)
);
SELECT add_audit_columns('maintenance_schedule');

-- ============================================================
-- Add new types to History entity_type enum
-- ============================================================
do $$ begin
    alter type entity_type add value if not exists 'SUPPLIER';
exception when duplicate_object then null;
end $$;

do $$ begin
    alter type entity_type add value if not exists 'PURCHASEORDER';
exception when duplicate_object then null;
end $$;

do $$ begin
    alter type entity_type add value if not exists 'PURCHASEORDERLINE';
exception when duplicate_object then null;
end $$;

do $$ begin
    alter type entity_type add value if not exists 'MAINTENANCESCHEDULE';
exception when duplicate_object then null;
end $$;

-- ============================================================
-- Indexes for performance
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_supplier_company ON supplier(company_id);
CREATE INDEX IF NOT EXISTS idx_purchase_order_company ON purchase_order(company_id);
CREATE INDEX IF NOT EXISTS idx_purchase_order_supplier ON purchase_order(supplier_id);
CREATE INDEX IF NOT EXISTS idx_purchase_order_line_order ON purchase_order_line(purchase_order_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_schedule_equipment ON maintenance_schedule(equipment_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_schedule_company ON maintenance_schedule(company_id);
