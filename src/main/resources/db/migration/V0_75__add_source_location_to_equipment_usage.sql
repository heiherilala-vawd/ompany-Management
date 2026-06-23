ALTER TABLE equipment_usage
ADD COLUMN IF NOT EXISTS source_location VARCHAR(150) CONSTRAINT eu_source_location_fk REFERENCES warehouse(id);
