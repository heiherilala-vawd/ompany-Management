INSERT INTO "income_money" (
  id,
  source_organization,
  invoice_reference,
  amount,
  description,
  billing_start_date,
  money_arrival_date,
  income_type_id,
  created_at,
  updated_at,
  job_id
)
VALUES
('income1_id', 'Client Alpha', 'INV-2024-001', 150000, 'Paiement initial chantier A', DATE '2024-01-15', DATE '2024-02-01', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income2_id', 'Client Beta', 'INV-2024-002', 275000, 'Paiement avance renovation hotel', DATE '2024-02-10', DATE '2024-02-15', 'income_type1_id', NOW(), NOW(), 'job1_id');
