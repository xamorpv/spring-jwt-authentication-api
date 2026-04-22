insert into users(username, password, email) values ('${usernames.admin}', '${passwords.standard}', 'egor_petrov_dfal8z@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = '${usernames.admin}'), (select id from authorities where authority = '${authorities.admin}'))
on conflict (user_id, authority_id) do nothing;