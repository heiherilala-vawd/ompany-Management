insert into task_schedule (id, title, description, priority, frequency, scheduled_date, status, company_id, created_at, updated_at)
values ('schedule1_id', 'Maintenance mensuelle', 'Nettoyage mensuel des équipements', 'MEDIUM', '0 0 8 1 * ?', '2026-07-01', 'PENDING', 'company1_id', now(), now());

insert into task_schedule_assigned_user (task_schedule_id, user_id)
values ('schedule1_id', 'employee1_id');
