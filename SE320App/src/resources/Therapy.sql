create table users
(
    id            char(36)                            not null
        primary key,
    user_type     enum ('PATIENT', 'DOCTOR', 'ADMIN') not null,
    first_name    varchar(100)                        not null,
    last_name     varchar(100)                        not null,
    username      varchar(100)                        not null,
    email         varchar(255)                        not null,
    password_hash varchar(255)                        not null,
    phone_number  int                                 null,
    constraint email
        unique (email),
    constraint username
        unique (username)
);

