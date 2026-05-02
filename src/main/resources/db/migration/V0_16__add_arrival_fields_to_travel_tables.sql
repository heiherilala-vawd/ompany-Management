-- Add arrival_location and arrival_date columns to travel tables (idempotent)
ALTER TABLE travel_equipment ADD COLUMN IF NOT EXISTS arrival_location VARCHAR(255);
ALTER TABLE travel_equipment ADD COLUMN IF NOT EXISTS arrival_date TIMESTAMP;
ALTER TABLE travel_materials ADD COLUMN IF NOT EXISTS arrival_location VARCHAR(255);
ALTER TABLE travel_materials ADD COLUMN IF NOT EXISTS arrival_date TIMESTAMP;
ALTER TABLE travel_people ADD COLUMN IF NOT EXISTS arrival_location VARCHAR(255);
ALTER TABLE travel_people ADD COLUMN IF NOT EXISTS arrival_date TIMESTAMP;

-- Add foreign key constraints only if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_travel_equipment_arrival_location') THEN
        ALTER TABLE travel_equipment ADD CONSTRAINT fk_travel_equipment_arrival_location FOREIGN KEY (arrival_location) REFERENCES warehouse(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_travel_materials_arrival_location') THEN
        ALTER TABLE travel_materials ADD CONSTRAINT fk_travel_materials_arrival_location FOREIGN KEY (arrival_location) REFERENCES warehouse(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_travel_people_arrival_location') THEN
        ALTER TABLE travel_people ADD CONSTRAINT fk_travel_people_arrival_location FOREIGN KEY (arrival_location) REFERENCES warehouse(id);
    END IF;
END $$;
