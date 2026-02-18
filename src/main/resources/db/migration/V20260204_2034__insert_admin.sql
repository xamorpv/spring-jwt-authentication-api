insert into users(username, password, email) values ('victor', '$2a$10$dOe3FX67uSD7jazWmHWnVumpBGLwCr0HtjUnntkUm4J20PliiBdpa', 'victor81-f8ak87@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = 'victor'), (select id from authorities where authority = 'ADMIN'))
on conflict (user_id, authority_id) do nothing;