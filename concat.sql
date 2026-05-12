INSERT INTO "employee_payment" (
  id, expense_id, employee_id, payment_description, payment_type
)
VALUES
('employee_payment1_id', 'expense1_id', 'employee1_id', 'Avance salaire chantier A', 'ADVANCE'),
('employee_payment2_id', 'expense2_id', 'user1_id', 'Paiement mensuel renovation', 'MONTHLY');
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
  id, expense_id, supplier_id, equipment, material, quantity, is_equipment
)
VALUES
('purchase1_id', 'expense1_id', 'warehouse1_id', 'equipment1_id', NULL, 1, true),
('purchase2_id', 'expense2_id', 'warehouse2_id', NULL, 'material2_id', 25, false);
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
       ('material3_id', 'warehouse_route_id', 0);-- Nettoyage des données de test (ordre FK inverse)
DELETE FROM "history";
DELETE FROM "travel_equipment";
DELETE FROM "travel_materials";
DELETE FROM "travel_people";
DELETE FROM "travel_expense";
DELETE FROM "other_expense";
DELETE FROM "bank_fee";
DELETE FROM "purchase";
DELETE FROM "employee_payment";
DELETE FROM "income_money";
DELETE FROM "income_type";
DELETE FROM "expense_money";
DELETE FROM "loan_repayment";
DELETE FROM "loan";
DELETE FROM "material_warehouse";
DELETE FROM "equipment";
DELETE FROM "material";
DELETE FROM "warehouse";
DELETE FROM "job";
DELETE FROM "company";
DELETE FROM "users";
INSERT INTO "loan" (id, amount, description, lender, interest_rate, start_date, status, job_id, created_at, updated_at)
VALUES
('loan1_id', 5000000, 'Emprunt construction entrepot', 'BNI Madagascar', 1200, DATE '2024-02-01', 'ACTIVE', 'job1_id', NOW(), NOW()),
('loan2_id', 3000000, 'Emprunt equipements', 'BOA Madagascar', 1500, DATE '2024-03-01', 'ACTIVE', 'job1_id', NOW(), NOW());
INSERT INTO "loan_repayment" (id, payment_date, amount, principal_portion, interest_portion, loan_id, created_at, updated_at)
VALUES
('repayment1_id', DATE '2024-03-01', 600000, 500000, 100000, 'loan1_id', NOW(), NOW()),
('repayment2_id', DATE '2024-04-01', 600000, 510000, 90000, 'loan1_id', NOW(), NOW());
-- Mots de passe hashés avec BCrypt (password = "admin123" pour tous)
-- Pour générer: BCryptPasswordEncoder().encode("admin123")

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES ('admin1_id', 'ADMIN', 'Admin', 'System', 'M', 'admin@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES ('warehouse1_id', 'WAREHOUSE_WORKER', 'Warehouse', 'Worker', 'M', 'warehouse@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES ('employee1_id', 'EMPLOYEE', 'John', 'Doe', 'M', 'employee@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES ('admin2_id', 'ADMINISTRATION', 'Admin', 'Staff', 'F', 'admin2@hei.school',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z');

INSERT INTO "users" (id, role, first_name, last_name, sex, email, password, created_at, updated_at)
VALUES
('user1_id', 'EMPLOYEE', 'Alice', 'Martin', 'F', 'alice@hei.school',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
 '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z'),
('user2_id', 'EMPLOYEE', 'Bob', 'Bernard', 'M', 'bob@hei.school',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
 '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z'),
('user3_id', 'WAREHOUSE_WORKER', 'Charlie', 'Durand', 'M', 'charlie@hei.school',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
 '2024-01-01T00:00:00Z', '2024-01-01T00:00:00Z');
INSERT INTO "company" (id, name, rib, description, company_type, created_at, updated_at)
VALUES
('company1_id', 'BTP Construction SARL', 'FR7612345678901234567890123', 'Entreprise de construction', 'BTP', NOW(), NOW()),
('company2_id', 'Hotel Palace', 'FR7698765432109876543210987', 'Hôtel de luxe', 'HOTEL', NOW(), NOW());
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
('warehouse_unfindable_id', 'Introuvable', 'Emplacement virtuel pour les équipements introuvables', NULL, NOW(), NOW());
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
INSERT INTO "material" (id, name, description, unit, created_at, updated_at)
VALUES
('material1_id', 'Ciment', 'Ciment Portland 35kg', 'SAC', NOW(), NOW()),
('material2_id', 'Brique', 'Brique rouge 20x10x5', 'U', NOW(), NOW()),
('material3_id', 'Peinture', 'Peinture blanche mate', 'L', NOW(), NOW());
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
('income2_id', 'Client Beta', 'INV-2024-002', 275000, 'Paiement avance renovation hotel', DATE '2024-02-10', DATE '2024-02-15', 'income_type1_id', NOW(), NOW(), 'job1_id'),
('income3_id', 'Etat', 'SUB-2024-001', 100000, 'Subvention travaux publics', DATE '2024-03-01', NULL, 'income_type2_id', NOW(), NOW(), 'job1_id'),
('income4_id', 'Donateur X', 'DON-2024-001', 50000, 'Don exceptionnel', DATE '2024-03-15', DATE '2024-03-20', 'income_type3_id', NOW(), NOW(), 'job1_id');
INSERT INTO "expense_money" (id, amount, description, created_at, updated_at, job_id)
VALUES
('expense1_id', 45000, 'Achat materiaux chantier A', NOW(), NOW(), 'job1_id'),
('expense2_id', 80000, 'Paiement sous-traitant renovation', NOW(), NOW(), 'job1_id');
