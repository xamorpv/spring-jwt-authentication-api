insert into users(username, password, email) values ('${usernames.moderator}', '${passwords.standard}', 'vlad_ivanov_819fda@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = '${usernames.moderator}'), (select id from authorities where authority = '${authorities.moderator}'))
on conflict (user_id, authority_id) do nothing;