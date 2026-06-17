CREATE TABLE IF NOT EXISTS travel_materials_arrival_log (
    id VARCHAR(150) PRIMARY KEY DEFAULT uuid_generate_v4(),
    travel_materials_id VARCHAR(150) NOT NULL REFERENCES travel_materials(id) ON DELETE CASCADE,
    quantity_received INTEGER NOT NULL DEFAULT 0,
    quantity_lost INTEGER NOT NULL DEFAULT 0,
    arrival_date TIMESTAMP NOT NULL DEFAULT NOW(),
    comment TEXT
);
SELECT add_audit_columns('travel_materials_arrival_log');
