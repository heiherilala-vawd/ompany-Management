insert into "leave" (id, user_id, leave_type_id, start_date, end_date, duration_days, status, reason, created_at, updated_at)
values
('leave1_id', 'employee1_id', 'leave_type1_id', '2026-06-01', '2026-06-15', 11.0, 'APPROVED', 'Vacances annuelles', now(), now()),
('leave2_id', 'employee1_id', 'leave_type2_id', '2026-03-10', '2026-03-12', 3.0, 'PENDING', 'Rendez-vous médical', now(), now());
