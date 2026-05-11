INSERT INTO "loan" (id, amount, description, lender, interest_rate, start_date, due_date, job_id, created_at, updated_at)
VALUES
('loan1_id', 5000000, 'Emprunt construction entrepot', 'BNI Madagascar', 1200, DATE '2024-02-01', DATE '2026-12-31', 'job1_id', NOW(), NOW()),
('loan2_id', 3000000, 'Emprunt equipements', 'BOA Madagascar', 1500, DATE '2024-03-01', NULL, 'job1_id', NOW(), NOW());
