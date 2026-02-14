alter table refresh_tokens
add column if not exists token_uuid varchar(64);