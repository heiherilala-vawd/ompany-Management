INSERT INTO "travel_people" (id, travel_id, person_name)
VALUES
('travel_people1_id', 'travel_expense1_id', 'Alice Martin'),
('travel_people2_id', 'travel_expense1_id', 'Bob Dupont');

INSERT INTO "travel_materials" (id, travel_id, material, quantity, quantity_received)
VALUES
('travel_materials1_id', 'travel_expense1_id', 'material1_id', 10, 5),
('travel_materials2_id', 'travel_expense2_id', 'material2_id', 20, NULL);

INSERT INTO "travel_equipment" (id, travel_id, equipment, quantity, status)
VALUES
('travel_equipment1_id', 'travel_expense1_id', 'equipment1_id', 2, 'IN_PROGRESS'),
('travel_equipment2_id', 'travel_expense2_id', 'equipment2_id', 1, 'ARRIVED');
