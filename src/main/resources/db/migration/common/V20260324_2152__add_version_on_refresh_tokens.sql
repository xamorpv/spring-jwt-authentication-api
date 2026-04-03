alter table refresh_tokens
add column if not exists version integer default 0;