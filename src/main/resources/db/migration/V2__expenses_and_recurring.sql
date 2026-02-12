-- =========================
-- Expenses
-- =========================
create table expenses (
    id bigserial primary key,

    user_id bigint not null,
    type varchar(255) not null,
    description varchar(500) not null,

    amount numeric(12,2) not null,
    date date not null,

    constraint fk_expenses_user
        foreign key (user_id) references app_user(id)
        on delete cascade
);

create index idx_expenses_user_id on expenses(user_id);
create index idx_expenses_date on expenses(date);
create index idx_expenses_user_date on expenses(user_id, date);

-- =========================
-- Recurring Expenses
-- =========================
create table recurring_expenses (
    id bigserial primary key,

    user_id bigint not null,

    type varchar(255) not null,
    description varchar(500) not null,

    amount_json varchar(10000) not null,
    date_json varchar(10000) not null,
    months_json varchar(1000) not null,

    day_of_the_month integer not null,

    constraint fk_recurring_expenses_user
        foreign key (user_id) references app_user(id)
        on delete cascade
);

create index idx_recurring_expenses_user_id on recurring_expenses(user_id);
