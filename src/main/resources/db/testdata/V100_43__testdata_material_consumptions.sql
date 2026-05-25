INSERT INTO material_consumption (id, material_id, warehouse_id, quantity, consumption_date, job_id, reason, consumption_status, created_at, updated_at, created_by, updated_by, comment)
VALUES
('mat_consumption1_id', 'material1_id', 'warehouse1_id', 10, '2024-06-01', 'job1_id', 'Utilisation pour fondation', 'COMPLETED', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('mat_consumption2_id', 'material1_id', 'warehouse1_id', 5, '2024-06-15', 'job1_id', 'Utilisation pour réparation', 'COMPLETED', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);
