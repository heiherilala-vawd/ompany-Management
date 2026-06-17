INSERT INTO "travel_people" (id, travel_id, user_id, arrival_location, arrival_date)
VALUES
('travel_people1_id', 'travel_expense1_id', 'employee1_id', 'warehouse1_id', '2024-03-01 12:00:00'),
('travel_people2_id', 'travel_expense1_id', 'employee1_id', 'warehouse2_id', '2024-03-05 15:00:00');

INSERT INTO "travel_materials" (id, travel_id, material, quantity, quantity_received)
VALUES
('travel_materials1_id', 'travel_expense1_id', 'material1_id', 10, 5),
('travel_materials2_id', 'travel_expense2_id', 'material2_id', 20, NULL);

INSERT INTO "travel_materials_arrival_log" (id, travel_materials_id, quantity_received, quantity_lost, arrival_date, arrival_location)
VALUES
('travel_mat_arrival_log1_id', 'travel_materials1_id', 5, 0, '2024-03-01 12:00:00', 'warehouse1_id');

INSERT INTO "travel_equipment" (id, travel_id, equipment, quantity, status, arrival_location, arrival_date)
VALUES
('travel_equipment1_id', 'travel_expense1_id', 'equipment1_id', 2, 'IN_PROGRESS', 'warehouse1_id', '2024-03-01 12:00:00'),
('travel_equipment2_id', 'travel_expense2_id', 'equipment2_id', 1, 'ARRIVED', 'warehouse2_id', '2024-03-05 15:00:00');
