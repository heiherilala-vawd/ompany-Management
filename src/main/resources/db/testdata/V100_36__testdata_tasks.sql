insert into task (id, title, description, due_date, priority, completed, completed_at, company_id, created_at, updated_at)
values
('task1_id', 'Vérifier le matériel', 'Inventaire du matériel sur le chantier A', '2026-06-15', 'HIGH', true, now(), 'company1_id', now(), now()),
('task2_id', 'Maintenance équipement', 'Maintenance mensuelle des équipements', '2026-07-01', 'MEDIUM', false, null, 'company1_id', now(), now());

insert into task_assignment (id, task_id, user_id, created_at, updated_at)
values
('task_assign1_id', 'task1_id', 'admin1_id', now(), now()),
('task_assign2_id', 'task1_id', 'employee1_id', now(), now()),
('task_assign3_id', 'task2_id', 'employee1_id', now(), now());
