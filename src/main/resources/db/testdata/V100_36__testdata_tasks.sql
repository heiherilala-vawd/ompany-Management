insert into task (id, title, description, due_date, priority, frequency, company_id, created_at, updated_at)
values
('task1_id', 'Vérifier le matériel', 'Inventaire du matériel sur le chantier A', '2026-06-15', 'HIGH', null, 'company1_id', now(), now()),
('task2_id', 'Maintenance équipement', 'Maintenance mensuelle des équipements', '2026-07-01', 'MEDIUM', '0 0 8 1 * ?', 'company1_id', now(), now());

insert into task_assignment (id, task_id, user_id, completed, completed_at, created_at, updated_at)
values
('task_assign1_id', 'task1_id', 'admin1_id', true, now(), now(), now()),
('task_assign2_id', 'task1_id', 'employee1_id', false, null, now(), now()),
('task_assign3_id', 'task2_id', 'employee1_id', false, null, now(), now());
