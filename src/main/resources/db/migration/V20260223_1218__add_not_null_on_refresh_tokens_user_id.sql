do $$
begin
    if (not exists (select 1 from information_schema.constraint_table_usage
    where table_schema = 'public' and table_name = 'refresh_tokens' and constraint_name = 'refresh_tokens_user_id_nn')) then
        delete from refresh_tokens where user_id is null; -- удаляем не пригодные для текущей бизнес логики данные - это не критичная потеря
        alter table refresh_tokens
        add constraint refresh_tokens_user_id_nn
        check(user_id is not null);
    end if;
end $$;