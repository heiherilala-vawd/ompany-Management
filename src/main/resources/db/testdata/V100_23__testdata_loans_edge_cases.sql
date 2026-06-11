INSERT INTO "loan" (id, amount, description, organization_id, interest_rate, start_date, due_date, job_id, created_at, updated_at)
VALUES
('loan3_id', 2000000, 'Emprunt rembourse', 'org1_id', 1000, DATE '2024-01-15', NULL, 'job1_id', NOW(), NOW()),
('loan4_id', 2000000, 'Emprunt en defaut', 'org1_id', 1000, DATE '2024-03-01', DATE '2024-06-01', 'job1_id', NOW(), NOW()),
('loan5_id', 2000000, 'Emprunt rembourse avant echeance', 'org1_id', 1000, DATE '2024-01-01', DATE '2024-06-01', 'job1_id', NOW(), NOW());
