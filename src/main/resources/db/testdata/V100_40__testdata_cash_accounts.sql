INSERT INTO cash_account (id, name, balance, description, company_id, created_at, updated_at, created_by, updated_by, comment)
VALUES
('cash_account1_id', 'Compte bancaire principal', 1000000.00, 'Compte courant BNI', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('cash_account2_id', 'Caisse', 500000.00, 'Caisse entreprise', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);
