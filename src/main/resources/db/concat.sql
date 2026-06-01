-- Nettoyage des données de test (ordre FK inverse)
DELETE FROM "maintenance_schedule";
DELETE FROM "purchase_order_line";
DELETE FROM "purchase_order";
DELETE FROM "supplier";
DELETE FROM "material_consumption";
DELETE FROM "equipment_usage";
DELETE FROM "cash_transaction";
DELETE FROM "cash_account";
DELETE FROM "budget_line";
DELETE FROM "department";
DELETE FROM "leave";
DELETE FROM "employee_leave_config";
DELETE FROM "leave_type";
DELETE FROM "history";
DELETE FROM "travel_equipment";
DELETE FROM "travel_materials";
DELETE FROM "travel_people";
DELETE FROM "travel_expense";
DELETE FROM "other_expense";
DELETE FROM "maintenance";
DELETE FROM "company_fixed_cost";
DELETE FROM "other_expense_type";
DELETE FROM "bank_fee";
DELETE FROM "purchase";
DELETE FROM "task_schedule_assigned_user";
DELETE FROM "task_schedule";
DELETE FROM "task_assignment";
DELETE FROM "task";
DELETE FROM "employee_payment_users";
DELETE FROM "employee_payment";
DELETE FROM "team_members";
DELETE FROM "team";
DELETE FROM "income_receipt";
DELETE FROM "income_money";
DELETE FROM "income_type";
DELETE FROM "expense_money";
DELETE FROM "loan_repayment";
DELETE FROM "loan";
DELETE FROM "material_warehouse";
DELETE FROM "equipment";
DELETE FROM "material";
DELETE FROM "warehouse";
DELETE FROM "user_job";
DELETE FROM "job";
DELETE FROM "users";
DELETE FROM "company";

INSERT INTO "company" (id, name, rib, description, company_type, created_at, updated_at)
VALUES
('company1_id', 'BTP Construction SARL', 'FR7612345678901234567890123', 'Entreprise de construction', 'BTP', NOW(), NOW()),
('company2_id', 'Hotel Palace', 'FR7698765432109876543210987', 'Hôtel de luxe', 'HOTEL', NOW(), NOW());

-- Mots de passe hashés avec BCrypt (password = "admin123" pour tous)
-- Pour générer: BCryptPasswordEncoder().encode("admin123")

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at, company_id)
VALUES ('admin1_id', 'ADMIN', 'Admin', 'System', 'M', 'admin@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at, company_id)
VALUES ('warehouse1_id', 'WAREHOUSE_WORKER', 'Warehouse', 'Worker', 'M', 'warehouse@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at, company_id)
VALUES ('employee1_id', 'EMPLOYEE', 'John', 'Doe', 'M', 'employee@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at, company_id)
VALUES ('admin2_id', 'ADMINISTRATION', 'Admin', 'Staff', 'F', 'admin2@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at, company_id)
VALUES
('user1_id', 'EMPLOYEE', 'Alice', 'Martin', 'F', 'alice@hei.school',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
 '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id'),
('user2_id', 'EMPLOYEE', 'Bob', 'Bernard', 'M', 'bob@hei.school',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
 '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id'),
('user3_id', 'WAREHOUSE_WORKER', 'Charlie', 'Durand', 'M', 'charlie@hei.school',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
 '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z', 'company1_id');

INSERT INTO "job" (id, company_id, description, contract_signature_date, start_date, end_date, status, created_at, updated_at)
VALUES
('job1_id', 'company1_id', 'Construction du bâtiment A', '2024-01-15', '2024-02-01', '2024-12-31', 'IN_PROGRESS', NOW(), NOW()),
('job2_id', 'company2_id', 'Rénovation des chambres', '2024-01-20', '2024-03-01', '2024-06-30', 'PENDING_SIGNATURE', NOW(), NOW());

INSERT INTO "warehouse" (id, name, description, job_id, created_at, updated_at)
VALUES
('warehouse1_id', 'Entrepôt Nord', 'Stockage matériaux lourds', 'job1_id', NOW(), NOW()),
('warehouse2_id', 'Entrepôt Sud', 'Stockage équipements', 'job2_id', NOW(), NOW()),
('warehouse_route_id', 'En route', 'Emplacement virtuel pour les équipements en déplacement', NULL, NOW(), NOW()),
('warehouse_at_seller_id', 'Chez le vendeur', 'Emplacement virtuel pour les équipements encore chez le vendeur', NULL, NOW(), NOW()),
('warehouse_unfindable_id', 'Introuvable', 'Emplacement virtuel pour les équipements introuvables', NULL, NOW(), NOW()),
('warehouse_used_id', 'Utilisé', 'Emplacement virtuel pour les matériaux/équipements utilisés', NULL, NOW(), NOW());

INSERT INTO "equipment" (id, name, description, warehouse_id, floor_number, storage_number, created_at, updated_at)
VALUES
('equipment1_id', 'Pelle mécanique', 'Pelle Caterpillar 320', 'warehouse1_id', 1, 10, NOW(), NOW()),
('equipment2_id', 'Bétonnière', 'Bétonnière électrique', 'warehouse1_id', 1, 15, NOW(), NOW()),
('equipment3_id', 'Climatisation', 'Unité extérieure', 'warehouse2_id', 2, 5, NOW(), NOW()),
('equipment4_id', 'Grue mobile', 'Grue Liebherr en route', 'warehouse_route_id', NULL, NULL, NOW(), NOW()),
('equipment5_id', 'Perceuse pro', 'Perceuse chez le vendeur', 'warehouse_at_seller_id', NULL, NULL, NOW(), NOW());

insert into "income_type" (id, name, description, company_id, created_at, updated_at)
values
('income_type1_id', 'Facturation client', 'Revenus issus de la facturation client', 'company1_id', now(), now()),
('income_type2_id', 'Subvention', 'Aides et subventions recues', 'company1_id', now(), now()),
('income_type3_id', 'Don', 'Dons et apports exceptionnels', 'company2_id', now(), now());

INSERT INTO "material" (id, name, description, unit, unit_price, created_at, updated_at, company_id)
VALUES
('material1_id', 'Ciment', 'Ciment Portland 35kg', 'SAC', 5000.00, NOW(), NOW(), 'company1_id'),
('material2_id', 'Brique', 'Brique rouge 20x10x5', 'U', 200.00, NOW(), NOW(), 'company1_id'),
('material3_id', 'Peinture', 'Peinture blanche mate', 'L', 15000.00, NOW(), NOW(), 'company1_id');

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
('income1_id', 'Client Alpha', 'INV-2024-001', 150000, 'Paiement initial chantier A', DATE '2024-01-15', DATE '2024-02-15', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income2_id', 'Client Beta', 'INV-2024-002', 275000, 'Paiement avance renovation hotel', DATE '2024-02-10', DATE '2024-03-12', 'NET-30', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income3_id', 'Etat', 'SUB-2024-001', 100000, 'Subvention travaux publics', DATE '2024-03-01', DATE '2024-06-01', 'NET-90', 'income_type2_id', NOW(), NOW(), 'job1_id'),
('income4_id', 'Donateur X', 'DON-2024-001', 50000, 'Don exceptionnel', DATE '2024-03-15', NULL, NULL, 'income_type3_id', NOW(), NOW(), 'job1_id');

INSERT INTO "expense_money" (id, amount, description, created_at, updated_at, job_id)
VALUES
('expense1_id', 45000, 'Achat materiaux chantier A', NOW(), NOW(), 'job1_id'),
('expense2_id', 80000, 'Paiement sous-traitant renovation', NOW(), NOW(), 'job1_id');

INSERT INTO "employee_payment" (
  id, expense_id, payment_description, payment_type
)
VALUES
('employee_payment1_id', 'expense1_id', 'Avance salaire chantier A', 'ADVANCE'),
('employee_payment2_id', 'expense2_id', 'Paiement mensuel renovation', 'MONTHLY');

INSERT INTO "travel_expense" (
  id, expense_id, departure_location, arrival_location, departure_date, arrival_date
)
VALUES
('travel_expense1_id', 'expense1_id', 'warehouse1_id', 'warehouse_route_id', '2024-03-01T06:00:00Z', '2024-03-01T12:00:00Z'),
('travel_expense2_id', 'expense2_id', 'warehouse2_id', 'warehouse1_id', '2024-03-05T07:30:00Z', '2024-03-05T15:00:00Z');

INSERT INTO "travel_people" (id, travel_id, user_id, arrival_location, arrival_date)
VALUES
('travel_people1_id', 'travel_expense1_id', 'employee1_id', 'warehouse1_id', '2024-03-01 12:00:00'),
('travel_people2_id', 'travel_expense1_id', 'employee1_id', 'warehouse2_id', '2024-03-05 15:00:00');

INSERT INTO "travel_materials" (id, travel_id, material, quantity, quantity_received, arrival_location, arrival_date)
VALUES
('travel_materials1_id', 'travel_expense1_id', 'material1_id', 10, 5, 'warehouse1_id', '2024-03-01 12:00:00'),
('travel_materials2_id', 'travel_expense2_id', 'material2_id', 20, NULL, 'warehouse2_id', '2024-03-05 15:00:00');

INSERT INTO "travel_equipment" (id, travel_id, equipment, quantity, status, arrival_location, arrival_date)
VALUES
('travel_equipment1_id', 'travel_expense1_id', 'equipment1_id', 2, 'IN_PROGRESS', 'warehouse1_id', '2024-03-01 12:00:00'),
('travel_equipment2_id', 'travel_expense2_id', 'equipment2_id', 1, 'ARRIVED', 'warehouse2_id', '2024-03-05 15:00:00');

INSERT INTO "purchase" (
  id, expense_id, source_warehouse_id, equipment, material, quantity, is_equipment, invoice_date, due_date, paid_at
)
VALUES
('purchase1_id', 'expense1_id', 'warehouse1_id', 'equipment1_id', NULL, 1, true, '2024-01-15', '2024-02-15', '2024-02-10'),
('purchase2_id', 'expense2_id', 'warehouse2_id', NULL, 'material2_id', 25, false, '2024-02-01', '2024-03-01', NULL);

INSERT INTO "bank_fee" (
  id, expense_id, bank_name, description
)
VALUES
('bank_fee1_id', 'expense1_id', 'BNI Madagascar', 'Frais virement fournisseur'),
('bank_fee2_id', 'expense2_id', 'BOA Madagascar', 'Commission paiement sous-traitant');

INSERT INTO "other_expense" (
  id, expense_id, description
)
VALUES
('other_expense1_id', 'expense1_id', 'Frais administratifs chantier A'),
('other_expense2_id', 'expense2_id', 'Imprevus renovation hotel');

-- Test data for history table
INSERT INTO history (id, previous_value, new_value, user_id, modified_at, entity_type, entity_id)
VALUES
    ('history1_id', '{"name": "Old Company Name"}', '{"name": "BTP Construction"}', 'admin1_id', '2024-01-15T10:30:00Z', 'COMPANY', 'company1_id'),
    ('history2_id', '{"description": "Old description"}', '{"description": "Entreprise de construction"}', 'admin1_id', '2024-01-16T14:00:00Z', 'COMPANY', 'company1_id'),
    ('history3_id', '{"status": "PENDING_SIGNATURE"}', '{"status": "IN_PROGRESS"}', 'admin2_id', '2024-02-01T09:00:00Z', 'JOB', 'job1_id'),
    ('history4_id', '{"firstName": "John"}', '{"firstName": "Johnny"}', 'admin1_id', '2024-02-10T11:30:00Z', 'USER', 'employee1_id'),
    ('history5_id', '{"quantity": 10}', '{"quantity": 15}', 'warehouse1_id', '2024-02-15T16:45:00Z', 'EQUIPMENT', 'equipment1_id');

INSERT INTO material_warehouse (material_id, warehouse_id, quantity)
VALUES ('material1_id', 'warehouse1_id', 100),
       ('material1_id', 'warehouse_route_id', 50),
       ('material2_id', 'warehouse_at_seller_id', 30),
       ('material3_id', 'warehouse_route_id', 0);
INSERT INTO "loan" (id, amount, description, lender, interest_rate, start_date, due_date, job_id, created_at, updated_at)
VALUES
('loan1_id', 5000000, 'Emprunt construction entrepot', 'BNI Madagascar', 1200, DATE '2024-02-01', DATE '2026-12-31', 'job1_id', NOW(), NOW()),
('loan2_id', 3000000, 'Emprunt equipements', 'BOA Madagascar', 1500, DATE '2024-03-01', NULL, 'job1_id', NOW(), NOW());

INSERT INTO "loan_repayment" (id, payment_date, amount, principal_portion, interest_portion, loan_id, created_at, updated_at)
VALUES
('repayment1_id', DATE '2024-03-01', 600000, 500000, 100000, 'loan1_id', NOW(), NOW()),
('repayment2_id', DATE '2024-04-01', 600000, 510000, 90000, 'loan1_id', NOW(), NOW());

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

INSERT INTO "loan" (id, amount, description, lender, interest_rate, start_date, due_date, job_id, created_at, updated_at)
VALUES
('loan3_id', 2000000, 'Emprunt rembourse', 'Microcred', 1000, DATE '2024-01-15', NULL, 'job1_id', NOW(), NOW()),
('loan4_id', 2000000, 'Emprunt en defaut', 'MBC Madagascar', 1000, DATE '2024-03-01', DATE '2024-06-01', 'job1_id', NOW(), NOW()),
('loan5_id', 2000000, 'Emprunt rembourse avant echeance', 'SIPEM', 1000, DATE '2024-01-01', DATE '2024-06-01', 'job1_id', NOW(), NOW());

INSERT INTO "loan_repayment" (id, payment_date, amount, principal_portion, interest_portion, loan_id, created_at, updated_at)
VALUES
('repayment3_id', DATE '2024-06-01', 2000000, 1800000, 200000, 'loan3_id', NOW(), NOW()),
('repayment5_id', DATE '2024-05-01', 2000000, 1800000, 200000, 'loan5_id', NOW(), NOW());

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

INSERT INTO income_receipt (id, payment_date, amount, income_id, created_at, updated_at)
VALUES
('receipt5_id',  DATE '2024-04-10',  60000,  'income5_id', NOW(), NOW()),
('receipt6a_id', DATE '2024-04-20',  60000,  'income6_id', NOW(), NOW()),
('receipt6b_id', DATE '2024-04-25',  60000,  'income6_id', NOW(), NOW()),
('receipt7a_id', DATE '2024-05-10',  60000,  'income7_id', NOW(), NOW()),
('receipt7b_id', DATE '2024-05-15',  40000,  'income7_id', NOW(), NOW());

-- User-job relationships are assigned dynamically via the API in tests
-- This file intentionally contains only the cleanup statement to clear the table before tests
DELETE FROM "user_job";

INSERT INTO employee_payment_users (employee_payment_id, user_id)
VALUES
('employee_payment1_id', 'employee1_id'),
('employee_payment2_id', 'user1_id');

INSERT INTO team (id, name, leader_id, created_at, updated_at, created_by, updated_by, comment)
VALUES
('team1_id', 'Équipe chantier A', 'employee1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', 'Équipe principale pour le chantier A'),
('team2_id', 'Équipe rénovation hôtel', 'user1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', 'Équipe pour la rénovation de l''hôtel');

INSERT INTO team_members (team_id, user_id)
VALUES
('team1_id', 'employee1_id'),
('team1_id', 'user1_id'),
('team1_id', 'user2_id'),
('team2_id', 'user1_id'),
('team2_id', 'employee1_id');

insert into "other_expense_type" (id, name, description, company_id, created_at, updated_at)
values
('other_exp_type1_id', 'Logistique', 'Frais logistiques et transport', 'company1_id', now(), now()),
('other_exp_type2_id', 'Administratif', 'Frais administratifs et bureau', 'company1_id', now(), now());

INSERT INTO "maintenance" (
  id, expense_id, equipment_id, description
)
VALUES
('maintenance1_id', 'expense1_id', 'equipment1_id', 'Revision moteur periodique'),
('maintenance2_id', 'expense2_id', 'equipment2_id', 'Remplacement pneus');

insert into "company_fixed_cost" (id, name, amount, description, company_id, start_date, end_date, created_at, updated_at)
values
('fixed_cost1_id', 'Loyer bureau', 2000.00, 'Loyer mensuel des locaux principaux', 'company1_id', '2024-01-01', null, now(), now()),
('fixed_cost2_id', 'Assurance vehicule', 500.00, 'Assurance flotte automobile', 'company1_id', '2024-03-01', '2025-03-01', now(), now());

insert into leave_type (id, name, description, paid, deduct_from_balance, color, days_per_year, company_id, created_at, updated_at)
values
('leave_type1_id', 'Congé payé', 'Congés annuels payés', true, true, '#4CAF50', 30, 'company1_id', now(), now()),
('leave_type2_id', 'Congé maladie', 'Arrêt maladie', true, false, '#F44336', null, 'company1_id', now(), now());

insert into employee_leave_config (id, company_id, hire_date, contract_type, vacation_days_per_month, created_at, updated_at)
values
('config1_id', 'company1_id', '2023-06-01', 'CDI', 2.5, now(), now()),
('config2_id', 'company1_id', '2024-01-15', 'CDD', 2.0, now(), now());

insert into "leave" (id, user_id, leave_type_id, start_date, end_date, duration_days, status, reason, created_at, updated_at)
values
('leave1_id', 'employee1_id', 'leave_type1_id', '2026-06-01', '2026-06-15', 11.0, 'APPROVED', 'Vacances annuelles', now(), now()),
('leave2_id', 'employee1_id', 'leave_type2_id', '2026-03-10', '2026-03-12', 3.0, 'PENDING', 'Rendez-vous médical', now(), now());

insert into task (id, title, description, due_date, priority, completed, completed_at, company_id, created_at, updated_at)
values
('task1_id', 'Vérifier le matériel', 'Inventaire du matériel sur le chantier A', '2026-06-15', 'HIGH', true, now(), 'company1_id', now(), now()),
('task2_id', 'Maintenance équipement', 'Maintenance mensuelle des équipements', '2026-07-01', 'MEDIUM', false, null, 'company1_id', now(), now());

insert into task_assignment (id, task_id, user_id, created_at, updated_at)
values
('task_assign1_id', 'task1_id', 'admin1_id', now(), now()),
('task_assign2_id', 'task1_id', 'employee1_id', now(), now()),
('task_assign3_id', 'task2_id', 'employee1_id', now(), now());

insert into task_schedule (id, title, description, priority, frequency, scheduled_date, status, company_id, created_at, updated_at)
values ('schedule1_id', 'Maintenance mensuelle', 'Nettoyage mensuel des équipements', 'MEDIUM', '0 0 8 1 * ?', '2026-07-01', 'PENDING', 'company1_id', now(), now());

insert into task_schedule_assigned_user (task_schedule_id, user_id)
values ('schedule1_id', 'employee1_id');

INSERT INTO department (id, name, description, company_id, created_at, updated_at, created_by, updated_by, comment)
VALUES
('department1_id', 'Génie Civil', 'Département en charge des travaux de génie civil', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', 'Département principal'),
('department2_id', 'Électricité', 'Département en charge des installations électriques', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);

INSERT INTO budget_line (id, company_id, category, planned_amount, actual_amount, period_start, period_end, description, created_at, updated_at, created_by, updated_by, comment)
VALUES
('budget_line1_id', 'company1_id', 'Matériaux', 500000.00, 450000.00, '2024-01-01', '2024-12-31', 'Budget matériaux construction', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('budget_line2_id', 'company1_id', 'Main-d''œuvre', 300000.00, 280000.00, '2024-01-01', '2024-12-31', 'Budget main-d''œuvre', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);

INSERT INTO cash_account (id, name, balance, description, company_id, created_at, updated_at, created_by, updated_by, comment)
VALUES
('cash_account1_id', 'Compte bancaire principal', 1000000.00, 'Compte courant BNI', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('cash_account2_id', 'Caisse', 500000.00, 'Caisse entreprise', 'company1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);

INSERT INTO cash_transaction (id, cash_account_id, amount, transaction_date, description, type, created_at, updated_at, created_by, updated_by, comment)
VALUES
('cash_txn1_id', 'cash_account1_id', 50000.00, '2024-06-01', 'Achat matériel bureau', 'DEBIT', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('cash_txn2_id', 'cash_account1_id', 200000.00, '2024-06-15', 'Virement client', 'CREDIT', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);

INSERT INTO equipment_usage (id, equipment_id, job_id, start_time, end_time, source_location, usage_status, used_by, created_at, updated_at, created_by, updated_by, comment)
VALUES
('equip_usage1_id', 'equipment1_id', 'job1_id', '2024-06-01 08:00:00+03', '2024-06-01 17:00:00+03', 'warehouse1_id', 'RETURNED', 'admin1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('equip_usage2_id', 'equipment1_id', 'job1_id', '2024-06-02 08:00:00+03', '2024-06-02 17:00:00+03', 'warehouse1_id', 'RETURNED', 'admin1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);

INSERT INTO material_consumption (id, material_id, warehouse_id, quantity, consumption_date, job_id, reason, consumption_status, created_at, updated_at, created_by, updated_by, comment)
VALUES
('mat_consumption1_id', 'material1_id', 'warehouse1_id', 10, '2024-06-01', 'job1_id', 'Utilisation pour fondation', 'COMPLETED', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('mat_consumption2_id', 'material1_id', 'warehouse1_id', 5, '2024-06-15', 'job1_id', 'Utilisation pour réparation', 'COMPLETED', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);

INSERT INTO "supplier" (id, name, siret, address, email, phone, contact_name, company_id, created_at, updated_at)
VALUES
('supplier1_id', 'Fournitures Pro', '12345678901234', '123 Rue du Commerce, Antananarivo', 'contact@fourniturespro.mg', '+261341234567', 'Jean Rajaonarison', 'company1_id', NOW(), NOW()),
('supplier2_id', 'Matériaux BTP', '98765432109876', '456 Avenue de l''Industrie, Toamasina', 'info@materiauxbtp.mg', '+261337654321', 'Marie Randrianarisoa', 'company1_id', NOW(), NOW());

INSERT INTO "purchase_order" (id, supplier_id, order_date, status, total_amount, company_id, job_id, created_at, updated_at)
VALUES
('po1_id', 'supplier1_id', '2024-06-01', 'VALIDATED', 150000.00, 'company1_id', 'job1_id', NOW(), NOW()),
('po2_id', 'supplier2_id', '2024-06-15', 'PENDING', 85000.00, 'company1_id', 'job1_id', NOW(), NOW());

INSERT INTO "purchase_order_line" (id, purchase_order_id, material_id, quantity, unit_price, created_at, updated_at)
VALUES
('pol1_id', 'po1_id', 'material1_id', 50, 1500.00, NOW(), NOW()),
('pol2_id', 'po1_id', 'material2_id', 200, 350.00, NOW(), NOW()),
('pol3_id', 'po2_id', 'material1_id', 20, 1600.00, NOW(), NOW());

INSERT INTO "maintenance_schedule" (id, equipment_id, description, scheduled_date, frequency, status, company_id, created_at, updated_at)
VALUES
('ms1_id', 'equipment1_id', 'Révision moteur périodique', '2024-07-15', 'MENSUEL', 'PENDING', 'company1_id', NOW(), NOW()),
('ms2_id', 'equipment2_id', 'Vidange et contrôle', '2024-08-01', 'TRIMESTRIEL', 'SCHEDULED', 'company1_id', NOW(), NOW());

