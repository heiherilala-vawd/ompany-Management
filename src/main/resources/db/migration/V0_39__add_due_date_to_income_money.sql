ALTER TABLE income_money ADD COLUMN IF NOT EXISTS due_date DATE;
ALTER TABLE income_money ADD COLUMN IF NOT EXISTS payment_terms VARCHAR(50);
CREATE INDEX IF NOT EXISTS idx_income_money_due_date ON income_money(due_date);
