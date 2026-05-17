do $$ begin
    alter type entity_type add value if not exists 'MAINTENANCE';
exception when duplicate_object then null;
end $$;
