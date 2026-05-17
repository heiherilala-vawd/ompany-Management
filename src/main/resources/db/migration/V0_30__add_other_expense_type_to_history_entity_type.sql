do $$ begin
    alter type entity_type add value if not exists 'OTHEREXPENSETYPE';
exception when duplicate_object then null;
end $$;
