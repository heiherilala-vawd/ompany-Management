ALTER TABLE travel_people ADD COLUMN IF NOT EXISTS user_id VARCHAR(150) REFERENCES "users"(id);
ALTER TABLE travel_people DROP COLUMN IF EXISTS person_name;

CREATE INDEX idx_travel_people_user_id ON travel_people(user_id);