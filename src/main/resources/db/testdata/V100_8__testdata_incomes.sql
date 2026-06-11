INSERT INTO "income_money" (
  id,
  organization_id,
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
('income1_id', 'org2_id', 'INV-2024-001', 150000, 'Paiement initial chantier A', DATE '2024-01-15', DATE '2024-02-15', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income2_id', 'org2_id', 'INV-2024-002', 275000, 'Paiement avance renovation hotel', DATE '2024-02-10', DATE '2024-03-12', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income3_id', 'org2_id', 'SUB-2024-001', 100000, 'Subvention travaux publics', DATE '2024-03-01', DATE '2024-06-01', 'NET-90', 'income_type2_id', NOW(), NOW(), 'job1_id'),
('income4_id', 'org2_id', 'DON-2024-001', 50000, 'Don exceptionnel', DATE '2024-03-15', NULL, NULL, 'income_type3_id', NOW(), NOW(), 'job1_id');
