-- Test data for history table
INSERT INTO history (id, previous_value, new_value, user_id, modified_at, entity_type, entity_id)
VALUES
    ('history1_id', '{"name": "Old Company Name"}', '{"name": "BTP Construction"}', 'admin1_id', '2024-01-15T10:30:00Z', 'COMPANY', 'company1_id'),
    ('history2_id', '{"description": "Old description"}', '{"description": "Entreprise de construction"}', 'admin1_id', '2024-01-16T14:00:00Z', 'COMPANY', 'company1_id'),
    ('history3_id', '{"status": "PENDING_SIGNATURE"}', '{"status": "IN_PROGRESS"}', 'admin2_id', '2024-02-01T09:00:00Z', 'JOB', 'job1_id'),
    ('history4_id', '{"firstName": "John"}', '{"firstName": "Johnny"}', 'admin1_id', '2024-02-10T11:30:00Z', 'USER', 'employee1_id'),
    ('history5_id', '{"quantity": 10}', '{"quantity": 15}', 'warehouse1_id', '2024-02-15T16:45:00Z', 'EQUIPMENT', 'equipment1_id');
