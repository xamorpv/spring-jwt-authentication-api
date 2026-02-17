insert into users(username, password, email) values ('oleg', '$2a$10$MsX7bj7cmIxd5y625IXWK.voWOOnTft8DAFDgec63DhzIcbqf127O', 'oleg81-jaf-184@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = 'oleg'), (select id from authorities where authority = 'USER'))
on conflict (user_id, authority_id) do nothing;