drop table if exists account;

create table account
(
    uid               text not null primary key,
    email             text not null,
    first_name        varchar(255),
    last_name         varchar(255),
    social_login      boolean default false,
    identity_provider text
);
