-- Add used warehouse for tracking consumed/used materials and equipment
INSERT INTO "warehouse" (id, name, description, job_id, created_at, updated_at)
VALUES ('warehouse_used_id', 'Utilisé', 'Emplacement virtuel pour les matériaux/équipements utilisés', NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Add consumption_status to material_consumption (IN_PROGRESS, COMPLETED)
ALTER TABLE material_consumption
ADD COLUMN IF NOT EXISTS consumption_status VARCHAR(50);

-- Add new columns to equipment_usage
ALTER TABLE equipment_usage
ADD COLUMN IF NOT EXISTS source_location VARCHAR(150) CONSTRAINT eu_source_location_fk REFERENCES warehouse(id),
ADD COLUMN IF NOT EXISTS usage_status VARCHAR(50),
ADD COLUMN IF NOT EXISTS used_by VARCHAR(150) CONSTRAINT eu_used_by_fk REFERENCES users(id);

-- Add job_id to material_warehouse for job-based stock filtering
ALTER TABLE material_warehouse
ADD COLUMN IF NOT EXISTS job_id VARCHAR(150) CONSTRAINT mw_job_fk REFERENCES job(id);
