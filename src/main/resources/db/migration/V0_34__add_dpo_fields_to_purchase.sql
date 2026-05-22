ALTER TABLE purchase ADD COLUMN IF NOT EXISTS invoice_date DATE NOT NULL DEFAULT NOW()::date;
ALTER TABLE purchase ADD COLUMN IF NOT EXISTS due_date DATE;
ALTER TABLE purchase ADD COLUMN IF NOT EXISTS paid_at DATE;
CREATE INDEX IF NOT EXISTS idx_purchase_invoice_date ON purchase(invoice_date);
CREATE INDEX IF NOT EXISTS idx_purchase_paid_at ON purchase(paid_at);
