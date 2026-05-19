insert into "company_fixed_cost" (id, name, amount, description, company_id, start_date, end_date, created_at, updated_at)
values
('fixed_cost1_id', 'Loyer bureau', 2000.00, 'Loyer mensuel des locaux principaux', 'company1_id', '2024-01-01', null, now(), now()),
('fixed_cost2_id', 'Assurance vehicule', 500.00, 'Assurance flotte automobile', 'company1_id', '2024-03-01', '2025-03-01', now(), now());
