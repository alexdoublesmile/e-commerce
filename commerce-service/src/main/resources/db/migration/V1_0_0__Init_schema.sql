CREATE TABLE product
(
    id      BIGSERIAL PRIMARY KEY,
    title   VARCHAR(64) NOT NULL,
    details VARCHAR(2048)
);