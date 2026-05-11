INSERT INTO "loan_repayment" (id, payment_date, amount, principal_portion, interest_portion, loan_id, created_at, updated_at)
VALUES
('repayment3_id', DATE '2024-06-01', 2000000, 1800000, 200000, 'loan3_id', NOW(), NOW()),
('repayment5_id', DATE '2024-05-01', 2000000, 1800000, 200000, 'loan5_id', NOW(), NOW());
