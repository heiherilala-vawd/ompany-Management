INSERT INTO "income_money" (id, source_organization, invoice_reference, amount, description, created_at, updated_at, job_id)
VALUES
('income1_id', 'Client Alpha', 'INV-2024-001', 150000, 'Paiement initial chantier A', NOW(), NOW(), 'job1_id'),
('income2_id', 'Client Beta', 'INV-2024-002', 275000, 'Paiement avance renovation hotel', NOW(), NOW(), 'job1_id');
