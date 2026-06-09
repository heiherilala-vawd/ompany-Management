ALTER TABLE travel_equipment
  ADD COLUMN departure_location VARCHAR(150)
  REFERENCES warehouse(id);

ALTER TABLE travel_materials
  ADD COLUMN departure_location VARCHAR(150)
  REFERENCES warehouse(id);
