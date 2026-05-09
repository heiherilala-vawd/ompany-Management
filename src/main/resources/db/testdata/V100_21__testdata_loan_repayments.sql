INSERT INTO "loan_repayment" (id, payment_date, amount, principal_portion, interest_portion, loan_id, created_at, updated_at)
VALUES
('repayment1_id', DATE '2024-03-01', 600000, 500000, 100000, 'loan1_id', NOW(), NOW()),
('repayment2_id', DATE '2024-04-01', 600000, 510000, 90000, 'loan1_id', NOW(), NOW());
