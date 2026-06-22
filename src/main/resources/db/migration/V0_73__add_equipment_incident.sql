-- Add is_damaged / is_lost booleans to equipment
ALTER TABLE equipment
    ADD COLUMN IF NOT EXISTS is_damaged  BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS is_lost     BOOLEAN NOT NULL DEFAULT false;

-- Create incident_type enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'incident_type') THEN
        CREATE TYPE incident_type AS ENUM ('DAMAGED', 'LOST');
    END IF;
END $$;

-- Create equipment_incident table
CREATE TABLE IF NOT EXISTS equipment_incident (
    id          VARCHAR(255) PRIMARY KEY,
    incident_type incident_type NOT NULL,
    equipment_id  VARCHAR(255) NOT NULL REFERENCES equipment(id) ON DELETE CASCADE,
    user_id       VARCHAR(255) REFERENCES users(id),
    travel_id     VARCHAR(255) REFERENCES travel_expense(id),
    location      VARCHAR(255),
    comment       TEXT,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_equipment_incident_equipment ON equipment_incident(equipment_id);

-- Data migration: set is_damaged/is_lost based on virtual warehouses
UPDATE equipment
SET is_damaged = true
WHERE warehouse_id = 'warehouse_damaged_id';

UPDATE equipment
SET is_lost = true
WHERE warehouse_id = 'warehouse_unfindable_id';

-- Data migration: move equipment out of virtual warehouses into a real one (warehouse1_id)
UPDATE equipment
SET warehouse_id = 'warehouse1_id'
WHERE warehouse_id IN ('warehouse_damaged_id', 'warehouse_unfindable_id');

-- Remove virtual warehouse rows
DELETE FROM warehouse WHERE id IN ('warehouse_damaged_id', 'warehouse_unfindable_id');

-- Add EQUIPMENTINCIDENT to entity_type enum
ALTER TYPE entity_type ADD VALUE IF NOT EXISTS 'EQUIPMENTINCIDENT';
