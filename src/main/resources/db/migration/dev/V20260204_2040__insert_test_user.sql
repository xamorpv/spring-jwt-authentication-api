insert into users(username, password, email) values ('${usernames.user}', '${passwords.standard}', 'oleg81-jaf-184@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = '${usernames.user}'), (select id from authorities where authority = '${authorities.user}'))
on conflict (user_id, authority_id) do nothing;