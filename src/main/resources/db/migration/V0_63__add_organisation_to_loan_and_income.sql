ALTER TABLE loan ADD COLUMN IF NOT EXISTS organisation_id VARCHAR(150) REFERENCES organisation(id);
ALTER TABLE income_money ADD COLUMN IF NOT EXISTS organisation_id VARCHAR(150) REFERENCES organisation(id);

CREATE INDEX IF NOT EXISTS idx_loan_organisation ON loan(organisation_id);
CREATE INDEX IF NOT EXISTS idx_income_money_organisation ON income_money(organisation_id);
