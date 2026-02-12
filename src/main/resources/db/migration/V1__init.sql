create table app_user (
    id bigserial primary key,

    nome varchar(255) not null,
    cognome varchar(255) not null,

    email varchar(255) not null unique,

    user_name varchar(255) not null unique,

    password_hash varchar(255) not null,

    tipo_utente integer not null
);
