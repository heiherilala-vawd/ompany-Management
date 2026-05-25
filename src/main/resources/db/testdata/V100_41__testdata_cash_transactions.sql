INSERT INTO cash_transaction (id, cash_account_id, amount, transaction_date, description, type, created_at, updated_at, created_by, updated_by, comment)
VALUES
('cash_txn1_id', 'cash_account1_id', 50000.00, '2024-06-01', 'Achat matériel bureau', 'DEBIT', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('cash_txn2_id', 'cash_account1_id', 200000.00, '2024-06-15', 'Virement client', 'CREDIT', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);
