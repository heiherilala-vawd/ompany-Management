alter table income_money
add column if not exists billing_start_date date,
add column if not exists money_arrival_date date,
add column if not exists income_type_id varchar(150);

alter table income_money
add constraint income_money_income_type_fk
foreign key (income_type_id) references income_type(id);

create index if not exists idx_income_money_income_type_id on income_money(income_type_id);
