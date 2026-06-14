INSERT INTO car (equipment_id, warehouse_id, license_plate, fuel_type, brand, model, year, color, mileage, status)
SELECT 'equipment1_id', 'warehouse1_id', 'AB-123-CD', 'DIESEL', 'Toyota', 'Hilux', 2020, 'Blanc', 50000, 'AVAILABLE'
WHERE NOT EXISTS (
    SELECT 1 FROM car WHERE equipment_id = 'equipment1_id' AND warehouse_id = 'warehouse1_id'
);

INSERT INTO car (equipment_id, warehouse_id, license_plate, fuel_type, brand, model, year, color, mileage, status)
SELECT 'equipment2_id', 'warehouse2_id', 'EF-456-GH', 'GASOLINE', 'Renault', 'Kangoo', 2021, 'Rouge', 30000, 'ON_MISSION'
WHERE NOT EXISTS (
    SELECT 1 FROM car WHERE equipment_id = 'equipment2_id' AND warehouse_id = 'warehouse2_id'
);
