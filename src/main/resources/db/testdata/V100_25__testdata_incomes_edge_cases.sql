INSERT INTO "income_money" (
  id,
  source_organization,
  invoice_reference,
  amount,
  description,
  billing_start_date,
  income_type_id,
  created_at,
  updated_at,
  job_id
)
VALUES
('income5_id', 'Client Delta', 'INV-2024-005', 100000, 'Paiement partiel', DATE '2024-04-01', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income6_id', 'Client Epsilon', 'INV-2024-006', 100000, 'Paiement en exces', DATE '2024-04-15', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income7_id', 'Client Zeta', 'INV-2024-007', 100000, 'Paiement total multiple recus', DATE '2024-05-01', 'income_type1_id', NOW(), NOW(), 'job1_id');
