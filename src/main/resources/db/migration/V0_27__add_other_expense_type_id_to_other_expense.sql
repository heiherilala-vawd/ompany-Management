ALTER TABLE other_expense ADD COLUMN IF NOT EXISTS other_expense_type_id VARCHAR(150);
ALTER TABLE other_expense ADD CONSTRAINT fk_other_expense_type FOREIGN KEY (other_expense_type_id) REFERENCES other_expense_type(id);
CREATE INDEX IF NOT EXISTS idx_other_expense_type_id ON other_expense(other_expense_type_id);
