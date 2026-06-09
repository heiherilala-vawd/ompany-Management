INSERT INTO equipment_usage (id, equipment_id, job_id, start_time, end_time, usage_status, used_by, created_at, updated_at, created_by, updated_by, comment)
VALUES
('equip_usage1_id', 'equipment1_id', 'job1_id', '2024-06-01 08:00:00+03', '2024-06-01 17:00:00+03', 'RETURNED', 'admin1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL),
('equip_usage2_id', 'equipment1_id', 'job1_id', '2024-06-02 08:00:00+03', '2024-06-02 17:00:00+03', 'RETURNED', 'admin1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', NULL);
