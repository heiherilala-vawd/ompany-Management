-- Add arrival_location and arrival_date columns to travel tables
ALTER TABLE travel_equipment ADD COLUMN arrival_location VARCHAR(255), ADD COLUMN arrival_date TIMESTAMP;
ALTER TABLE travel_materials ADD COLUMN arrival_location VARCHAR(255), ADD COLUMN arrival_date TIMESTAMP;
ALTER TABLE travel_people ADD COLUMN arrival_location VARCHAR(255), ADD COLUMN arrival_date TIMESTAMP;

-- Add foreign key constraints
ALTER TABLE travel_equipment ADD CONSTRAINT fk_travel_equipment_arrival_location FOREIGN KEY (arrival_location) REFERENCES warehouse(id);
ALTER TABLE travel_materials ADD CONSTRAINT fk_travel_materials_arrival_location FOREIGN KEY (arrival_location) REFERENCES warehouse(id);
ALTER TABLE travel_people ADD CONSTRAINT fk_travel_people_arrival_location FOREIGN KEY (arrival_location) REFERENCES warehouse(id);
