do $$
begin
    if (not exists (select 1 from information_schema.constraint_table_usage
    where table_schema = 'public' and table_name = 'refresh_tokens' and constraint_name = 'refresh_tokens_compromised_nn')) then
        alter table refresh_tokens
        add constraint refresh_tokens_compromised_nn
        check(compromised is not null);
    end if;

    if (not exists (select 1 from information_schema.constraint_table_usage
        where table_schema = 'public' and table_name = 'refresh_tokens' and constraint_name = 'refresh_tokens_used_nn')) then
            alter table refresh_tokens
            add constraint refresh_tokens_used_nn
            check(used is not null);
        end if;
end $$;