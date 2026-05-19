INSERT INTO "income_money" (
  id,
  source_organization,
  invoice_reference,
  amount,
  description,
  billing_start_date,
  due_date,
  payment_terms,
  income_type_id,
  created_at,
  updated_at,
  job_id
)
VALUES
('income5_id', 'Client Delta', 'INV-2024-005', 100000, 'Paiement partiel', DATE '2024-04-01', DATE '2024-05-01', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income6_id', 'Client Epsilon', 'INV-2024-006', 100000, 'Paiement en exces', DATE '2024-04-15', DATE '2024-05-15', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income7_id', 'Client Zeta', 'INV-2024-007', 100000, 'Paiement total multiple recus', DATE '2024-05-01', DATE '2024-05-31', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id');
