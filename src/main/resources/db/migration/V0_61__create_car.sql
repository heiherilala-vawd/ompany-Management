DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'fuel_type') THEN
    CREATE TYPE fuel_type AS ENUM ('DIESEL', 'GASOLINE', 'ELECTRIC', 'HYBRID', 'LPG');
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'car_status') THEN
    CREATE TYPE car_status AS ENUM ('ON_MISSION', 'AVAILABLE', 'UNDER_MAINTENANCE', 'OUT_OF_SERVICE');
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS car (
    equipment_id VARCHAR(150) CONSTRAINT car_equipment_fk REFERENCES equipment(id),
    warehouse_id VARCHAR(150) CONSTRAINT car_warehouse_fk REFERENCES warehouse(id),
    license_plate VARCHAR(50),
    fuel_type fuel_type,
    brand VARCHAR(100),
    model VARCHAR(100),
    year INTEGER,
    color VARCHAR(50),
    mileage INTEGER,
    status car_status,
    PRIMARY KEY (equipment_id, warehouse_id)
);

SELECT add_audit_columns('car');
