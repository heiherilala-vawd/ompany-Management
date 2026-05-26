INSERT INTO "purchase_order" (id, supplier_id, order_date, status, total_amount, company_id, job_id, created_at, updated_at)
VALUES
('po1_id', 'supplier1_id', '2024-06-01', 'VALIDATED', 150000.00, 'company1_id', 'job1_id', NOW(), NOW()),
('po2_id', 'supplier2_id', '2024-06-15', 'PENDING', 85000.00, 'company1_id', 'job1_id', NOW(), NOW());

INSERT INTO "purchase_order_line" (id, purchase_order_id, material_id, quantity, unit_price, created_at, updated_at)
VALUES
('pol1_id', 'po1_id', 'material1_id', 50, 1500.00, NOW(), NOW()),
('pol2_id', 'po1_id', 'material2_id', 200, 350.00, NOW(), NOW()),
('pol3_id', 'po2_id', 'material1_id', 20, 1600.00, NOW(), NOW());
