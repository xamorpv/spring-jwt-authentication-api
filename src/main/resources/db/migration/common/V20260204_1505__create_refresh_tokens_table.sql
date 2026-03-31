create table if not exists refresh_tokens (
    id bigserial primary key,
    token varchar(128) unique not null
);