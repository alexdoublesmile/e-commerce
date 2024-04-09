CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(128)  NOT NULL UNIQUE,
    password VARCHAR(2048) NOT NULL
);

CREATE TABLE authority
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE user_authority
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    authority_id INT    NOT NULL REFERENCES authority(id),
    CONSTRAINT uk_user_authority UNIQUE (user_id, authority_id)
);