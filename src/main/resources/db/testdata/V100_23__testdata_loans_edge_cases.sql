INSERT INTO "loan" (id, amount, description, lender, interest_rate, start_date, due_date, job_id, created_at, updated_at)
VALUES
('loan3_id', 2000000, 'Emprunt rembourse', 'Microcred', 1000, DATE '2024-01-15', NULL, 'job1_id', NOW(), NOW()),
('loan4_id', 2000000, 'Emprunt en defaut', 'MBC Madagascar', 1000, DATE '2024-03-01', DATE '2024-06-01', 'job1_id', NOW(), NOW()),
('loan5_id', 2000000, 'Emprunt rembourse avant echeance', 'SIPEM', 1000, DATE '2024-01-01', DATE '2024-06-01', 'job1_id', NOW(), NOW());
