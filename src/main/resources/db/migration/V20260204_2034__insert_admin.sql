insert into users(username, password, email) values ('${admin.name}', '${admin.password}', '${admin.email}')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = '${admin.name}'), (select id from authorities where authority = '${authorities.admin}'))
on conflict (user_id, authority_id) do nothing;