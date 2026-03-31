alter table refresh_tokens
add column if not exists compromised boolean default false;