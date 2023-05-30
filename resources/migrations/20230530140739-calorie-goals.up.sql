create table user_goal
(
    user_id              text unique not null references account (uid) on delete cascade,
    total_daily_calories int,
    total_daily_proteins int
);
