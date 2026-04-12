insert into users(username, password, email) values ('egor', '$2a$10$rE6rezoRPvELyStNQMs1G.aTaBrSie05qQmYW1DRqMYYz.E2iYT9S', 'egor_petrov_dfal8z@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = 'egor'), (select id from authorities where authority = '${authorities.admin}'))
on conflict (user_id, authority_id) do nothing;