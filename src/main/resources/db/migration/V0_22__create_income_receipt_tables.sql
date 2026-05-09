do
$$
begin
    alter type entity_type add value if not exists 'INCOMERECEIPT';
exception
    when duplicate_object then null;
end
$$;

create table if not exists income_receipt (
    id VARCHAR(150) constraint income_receipt_pk primary key,
    payment_date DATE NOT NULL,
    amount INTEGER NOT NULL,
    income_id VARCHAR(150) NOT NULL constraint income_receipt_income_fk references income_money(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(150) constraint income_receipt_created_by_fk references users(id),
    updated_by VARCHAR(150) constraint income_receipt_updated_by_fk references users(id),
    comment TEXT
);

create index if not exists idx_income_receipt_income_id on income_receipt(income_id);
