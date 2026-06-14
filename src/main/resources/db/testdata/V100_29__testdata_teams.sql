INSERT INTO team (id, name, leader_id, job_id, created_at, updated_at, created_by, updated_by, comment)
VALUES
('team1_id', 'Équipe chantier A', 'employee1_id', 'job1_id', NOW(), NOW(), 'admin1_id', 'admin1_id', 'Équipe principale pour le chantier A'),
('team2_id', 'Équipe rénovation hôtel', 'user1_id', NULL, NOW(), NOW(), 'admin1_id', 'admin1_id', 'Équipe pour la rénovation de l''hôtel');

INSERT INTO team_members (team_id, user_id)
VALUES
('team1_id', 'employee1_id'),
('team1_id', 'user1_id'),
('team1_id', 'user2_id'),
('team2_id', 'user1_id'),
('team2_id', 'employee1_id');
