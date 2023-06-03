create table telegram_user
(
    id            numeric unique,
    first_name    varchar(255) not null,
    is_bot        bool,
    language_code varchar(255),
    last_name     varchar(255),
    username      varchar(255)
);
