ALTER TABLE travel_materials_arrival_log
  ADD COLUMN IF NOT EXISTS arrival_location VARCHAR(150) REFERENCES warehouse(id);

UPDATE travel_materials_arrival_log tml
SET arrival_location = tm.arrival_location
FROM travel_materials tm
WHERE tml.travel_materials_id = tm.id
  AND tml.arrival_location IS NULL;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'travel_materials_arrival_log'
    AND column_name = 'arrival_location'
    AND is_nullable = 'YES'
  ) THEN
    ALTER TABLE travel_materials_arrival_log ALTER COLUMN arrival_location SET NOT NULL;
  END IF;
END $$;

ALTER TABLE travel_materials DROP COLUMN IF EXISTS arrival_location;
ALTER TABLE travel_materials DROP COLUMN IF EXISTS arrival_date;
