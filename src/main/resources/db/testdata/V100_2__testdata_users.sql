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
