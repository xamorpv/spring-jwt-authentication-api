insert into users(username, password, email) values ('vlad', '$2a$10$rE6rezoRPvELyStNQMs1G.aTaBrSie05qQmYW1DRqMYYz.E2iYT9S', 'vlad_ivanov_819fda@gmail.com')
on conflict(username) do nothing;
insert into users_authorities(user_id, authority_id) values((select id from users where username = 'vlad'), (select id from authorities where authority = '${authorities.moderator}'))
on conflict (user_id, authority_id) do nothing;