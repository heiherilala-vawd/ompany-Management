INSERT INTO "maintenance_schedule" (id, equipment_id, description, scheduled_date, frequency, status, company_id, created_at, updated_at)
VALUES
('ms1_id', 'equipment1_id', 'Révision moteur périodique', '2024-07-15', 'MENSUEL', 'PENDING', 'company1_id', NOW(), NOW()),
('ms2_id', 'equipment2_id', 'Vidange et contrôle', '2024-08-01', 'TRIMESTRIEL', 'SCHEDULED', 'company1_id', NOW(), NOW());
