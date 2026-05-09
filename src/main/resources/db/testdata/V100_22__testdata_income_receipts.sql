INSERT INTO income_receipt (
  id,
  payment_date,
  amount,
  income_id,
  created_at,
  updated_at
)
VALUES
('receipt1_id', DATE '2024-02-01', 150000, 'income1_id', NOW(), NOW()),
('receipt2_id', DATE '2024-02-15', 275000, 'income2_id', NOW(), NOW()),
('receipt3_id', DATE '2024-03-20', 50000, 'income4_id', NOW(), NOW());
