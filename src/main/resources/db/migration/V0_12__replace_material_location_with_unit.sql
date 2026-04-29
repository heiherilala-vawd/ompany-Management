DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'material_unit') THEN
    CREATE TYPE material_unit AS ENUM (
      'SAC', 'L', 'KG', 'M2', 'M3', 'KIT', 'POT', 'PNL', 'FEU', 'BAR', 'T', 'M', 'FFT', 'U'
    );
  END IF;
END $$;

ALTER TABLE material DROP CONSTRAINT IF EXISTS material_warehouse_pk;
ALTER TABLE material DROP COLUMN IF EXISTS warehouse_id;
ALTER TABLE material DROP COLUMN IF EXISTS floor_number;
ALTER TABLE material DROP COLUMN IF EXISTS storage_number;
ALTER TABLE material ADD COLUMN IF NOT EXISTS unit material_unit;
