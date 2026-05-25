INSERT INTO budget_line (id, company_id, category, planned_amount, actual_amount, period_start, period_end, description, created_at, updated_at, created_by, updated_by, comment)
VALUES
('budget_line1_id', 'company1_id', 'Matériaux', 500000.00, 450000.00, '2024-01-01', '2024-12-31', 'Budget matériaux construction', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('budget_line2_id', 'company1_id', 'Main-d''œuvre', 300000.00, 280000.00, '2024-01-01', '2024-12-31', 'Budget main-d''œuvre', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);
