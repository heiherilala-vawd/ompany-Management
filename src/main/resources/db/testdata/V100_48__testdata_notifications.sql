insert into notification (id, user_id, task_id, title, message, read, completed, created_at, updated_at)
values
('notif_admin_unread', 'admin1_id', null, 'Nouveau rapport', 'Le rapport mensuel est disponible', false, false, now(), now()),
('notif_admin_read', 'admin1_id', null, 'Rapport consulté', 'Vous avez consulté le rapport', true, false, now(), now()),
('notif_admin_completed', 'admin1_id', null, 'Tâche terminée', 'La tâche de maintenance est terminée', false, true, now(), now()),
('notif_admin_task', 'admin1_id', 'task1_id', 'Tâche assignée', 'Vous avez été assigné à une tâche', false, false, now(), now()),
('notif_employee_unread', 'employee1_id', null, 'Nouvelle mission', 'Une nouvelle mission vous est assignée', false, false, now(), now()),
('notif_employee_read', 'employee1_id', null, 'Mission consultée', 'Vous avez consulté la mission', true, false, now(), now());
