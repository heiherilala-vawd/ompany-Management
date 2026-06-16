ALTER TYPE transport_status ADD VALUE IF NOT EXISTS 'DAMAGED';

INSERT INTO "warehouse" (id, name, description, job_id, created_at, updated_at)
VALUES ('warehouse_damaged_id', 'Détérioré', 'Emplacement virtuel pour les équipements détériorés à l''arrivée', NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
