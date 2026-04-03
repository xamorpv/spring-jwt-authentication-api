alter table refresh_tokens
add column if not exists used boolean default false;
alter table refresh_tokens
add column if not exists used_at timestamptz;
alter table refresh_tokens
add column if not exists user_id bigint references users(id);