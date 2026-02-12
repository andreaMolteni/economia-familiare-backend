-- =========================
-- Income
-- =========================
create table income (
    id bigserial primary key,

    user_id bigint not null,
    type varchar(255) not null,
    description varchar(500) not null,

    amount numeric(12,2) not null,
    date date not null,

    constraint fk_income_user
        foreign key (user_id) references app_user(id)
        on delete cascade
);

create index idx_income_user_id on income(user_id);
create index idx_income_date on income(date);
create index idx_income_user_date on income(user_id, date);

-- =========================
-- Recurring Income
-- =========================
create table recurring_income (
    id bigserial primary key,

    user_id bigint not null,

    type varchar(255) not null,
    description varchar(500) not null,

    amount_json varchar(10000) not null,
    date_json varchar(10000) not null,
    months_json varchar(1000) not null,

    day_of_the_month integer not null,

    constraint fk_recurring_income_user
        foreign key (user_id) references app_user(id)
        on delete cascade
);

create index idx_recurring_income_user_id on recurring_income(user_id);