-- Rename siret to company_registration_number in supplier table
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'supplier' AND column_name = 'siret') THEN
    ALTER TABLE supplier RENAME COLUMN siret TO company_registration_number;
  END IF;
END $$;

-- Remove VARCHAR(14) restriction — companies outside France can have longer registration numbers
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'supplier' AND column_name = 'company_registration_number' AND character_maximum_length != 255) THEN
    ALTER TABLE supplier ALTER COLUMN company_registration_number TYPE VARCHAR(255);
  END IF;
END $$;
