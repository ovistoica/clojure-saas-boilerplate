create table telegram_updates
(
    chat_id        numeric not null primary key,
    last_update_id numeric
);
