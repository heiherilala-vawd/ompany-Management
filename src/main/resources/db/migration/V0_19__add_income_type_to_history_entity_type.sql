do
$$
begin
    alter type entity_type add value if not exists 'INCOMETYPE';
exception
    when duplicate_object then null;
end
$$;
