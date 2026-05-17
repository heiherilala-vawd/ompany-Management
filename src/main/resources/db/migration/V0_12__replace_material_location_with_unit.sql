DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'material_unit') THEN
    CREATE TYPE material_unit AS ENUM (
      'SAC', 'L', 'KG', 'M2', 'M3', 'KIT', 'POT', 'PNL', 'FEU', 'BAR', 'T', 'M', 'FFT', 'U'
    );
  END IF;
END $$;
