CREATE TABLE calories
(
    id         SERIAL PRIMARY KEY,
    user_id    numeric not null references telegram_user (id) on delete cascade,
    food_input text    not null,
    log_time   TIMESTAMP WITH TIME ZONE,
    calories   numeric not null,
    proteins   numeric not null default 0,
    carbs      numeric not null default 0,
    fat        numeric not null default 0
);