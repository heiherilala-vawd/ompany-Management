INSERT INTO "purchase" (
  id, expense_id, supplier_id, equipment, material, quantity, is_equipment, invoice_date, due_date, paid_at
)
VALUES
('purchase1_id', 'expense1_id', 'warehouse1_id', 'equipment1_id', NULL, 1, true, '2024-01-15', '2024-02-15', '2024-02-10'),
('purchase2_id', 'expense2_id', 'warehouse2_id', NULL, 'material2_id', 25, false, '2024-02-01', '2024-03-01', NULL);
