insert into leave_type (id, name, description, paid, deduct_from_balance, color, days_per_year, company_id, created_at, updated_at)
values
('leave_type1_id', 'Congé payé', 'Congés annuels payés', true, true, '#4CAF50', 30, 'company1_id', now(), now()),
('leave_type2_id', 'Congé maladie', 'Arrêt maladie', true, false, '#F44336', null, 'company1_id', now(), now());
