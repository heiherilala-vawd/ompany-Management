-- =========================
-- ENUM entity_type
-- =========================
do
$$
begin
    if not exists(select from pg_type where typname = 'entity_type') then
        create type entity_type as enum (
            'USER',
            'COMPANY',
            'JOB',
            'WAREHOUSE',
            'EQUIPMENT',
            'MATERIAL',
            'INCOMEMONEY',
            'EXPENSEMONEY',
            'EMPLOYEE_PAYMENT',
            'TRAVEL_EXPENSE',
            'TRAVELPEOPLE',
            'TRAVELMATERIALS',
            'TRAVELEQUIPMENT',
            'PURCHASE',
            'BANK_FEE',
            'OTHER_EXPENSE'
        );
    end if;
end
$$;

-- =========================
-- TABLE history
-- =========================
create table if not exists history (
    id VARCHAR(150) constraint history_pk primary key default uuid_generate_v4(),
    previous_value TEXT,
    new_value TEXT,
    user_id VARCHAR(150) constraint history_user_fk references users(id),
    modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    entity_type entity_type NOT NULL,
    entity_id VARCHAR(150) NOT NULL
);

-- Index pour améliorer les performances des requêtes filtrées
create index if not exists idx_history_user_id on history(user_id);
create index if not exists idx_history_entity_type on history(entity_type);
create index if not exists idx_history_entity_id on history(entity_id);
create index if not exists idx_history_modified_at on history(modified_at);
