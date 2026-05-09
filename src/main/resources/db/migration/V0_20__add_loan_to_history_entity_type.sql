do
$$
begin
    alter type entity_type add value if not exists 'LOAN';
exception
    when duplicate_object then null;
end
$$;
do
$$
begin
    alter type entity_type add value if not exists 'LOANREPAYMENT';
exception
    when duplicate_object then null;
end
$$;
