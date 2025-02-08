CREATE TABLE app_users
(
    user_id          SERIAL PRIMARY KEY,
    access_token     VARCHAR(60)  NOT NULL,
    auth_token       VARCHAR(60)  NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    name             VARCHAR(100) NOT NULL,
    salt             VARCHAR(60),
    password         VARCHAR(60)  NOT NULL,
    verified_account BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    deleted_at       TIMESTAMP(6)
);



