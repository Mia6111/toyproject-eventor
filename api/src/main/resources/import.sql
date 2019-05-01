
 -- --
insert INTO account (id ,deleted, email, account_name, password) values (1, FALSE , 'test@abc.com', 'mia', '$2a$10$A3zMoDdS6EUD8n97tekT1./izhO2SrVRDD9Bl2nNKf7r.GhATa8j6');
insert into account (id ,deleted, email, account_name, password) values (2, FALSE , '2test@abc.com', 'other', '$2a$10$5A2xPD8kDQr67LjiJf3M4.tiz262zesDV6QTHQAgq3spvcEs86GG.');

insert into event (id, deleted, content, event_end_date, event_start_date, location, max_people_cnt, price, register_end_date,register_start_date, title, host_account_id) values (1, false, 'CONTENT', {ts '2019-02-25'}, {ts '2019-02-20'}, 'LOCATION', 10, 10000,{ts '2019-02-15'}, {ts '2019-02-01'}, 'TITLE', 1);
