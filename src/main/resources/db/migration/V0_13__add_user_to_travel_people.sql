ALTER TABLE travel_people ADD COLUMN IF NOT EXISTS user_id VARCHAR(150) REFERENCES "users"(id);

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='travel_people' AND column_name='person_name') THEN
    ALTER TABLE travel_people DROP COLUMN person_name;
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_travel_people_user_id ON travel_people(user_id);