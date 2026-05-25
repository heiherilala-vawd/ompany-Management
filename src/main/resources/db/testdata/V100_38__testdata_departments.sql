INSERT INTO department (id, name, description, company_id, created_at, updated_at, created_by, updated_by, comment)
VALUES
('department1_id', 'Génie Civil', 'Département en charge des travaux de génie civil', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', 'Département principal'),
('department2_id', 'Électricité', 'Département en charge des installations électriques', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);
