INSERT INTO "job" (id, company_id, description, contract_signature_date, start_date, end_date, status, created_at, updated_at)
VALUES
('job1_id', 'company1_id', 'Construction du bâtiment A', '2024-01-15', '2024-02-01', '2024-12-31', 'IN_PROGRESS', NOW(), NOW()),
('job2_id', 'company2_id', 'Rénovation des chambres', '2024-01-20', '2024-03-01', '2024-06-30', 'PENDING_SIGNATURE', NOW(), NOW());
