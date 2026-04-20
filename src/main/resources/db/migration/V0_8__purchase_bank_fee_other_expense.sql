create table if not exists purchase (
                                        id VARCHAR(150) constraint purchase_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint purchase_expense_money_pk REFERENCES expense_money(id),
    supplier VARCHAR(150),
    equipment varchar not null constraint purchase_equipment_id_fk references "equipment"(id),
    material varchar not null constraint purchase_material_id_fk references "material"(id),
    quantity integer not null,
    is_equipment BOOLEAN
    );

create table if not exists bank_fee (
                                        id VARCHAR(150) constraint bank_fee_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint bank_fee_expense_money_pk REFERENCES expense_money(id),
    bank_name VARCHAR(150),
    description TEXT
    );

create table if not exists other_expense (
                                             id VARCHAR(150) constraint other_expense_pk  primary key default uuid_generate_v4(),
    expense_id VARCHAR(150) constraint other_expense_expense_money_pk REFERENCES expense_money(id),
    description TEXT
    );
