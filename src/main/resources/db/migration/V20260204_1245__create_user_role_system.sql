create table if not exists users (
    id bigserial primary key,
    username varchar(48) unique not null,
    email varchar(255) unique not null,
    password varchar(128) not null,
    account_non_expired boolean default false,
    account_non_locked boolean default false,
    credentials_non_expired boolean default false,
    enabled boolean default false
);

create table if not exists authorities (
    id bigserial primary key,
    authority text unique not null
);

create table if not exists users_authorities (
    user_id bigint references users(id),
    authority_id bigint references authorities(id),
    constraint pk_user_authority primary key (user_id, authority_id)
);