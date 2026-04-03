alter table refresh_tokens
add column created_at timestamptz default current_timestamp;