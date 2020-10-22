-- CREATE DATABASE winestyle;

CREATE TABLE alcohol
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(130),
    type         VARCHAR(20),
    url          VARCHAR(135),
    image_url    VARCHAR(65),
    crop_year    INTEGER,
    manufacturer VARCHAR(65),
    brand        VARCHAR(50),
    price        REAL,
    volume       REAL,
    rating       REAL,
    country      VARCHAR(15),
    region       VARCHAR(70),
    color        VARCHAR(15),
    grape        VARCHAR(110),
    sugar        VARCHAR(50),
    strength     VARCHAR(30),
    aroma        TEXT,
    taste        TEXT,
    food_pairing TEXT,
    description  TEXT
);

CREATE TABLE error_on_saving
(
    id           SERIAL PRIMARY KEY,
    name         TEXT,
    type         TEXT,
    url          TEXT,
    image_url    TEXT,
    crop_year    TEXT,
    manufacturer TEXT,
    brand        TEXT,
    price        TEXT,
    volume       TEXT,
    rating       TEXT,
    country      TEXT,
    region       TEXT,
    color        TEXT,
    grape        TEXT,
    sugar        TEXT,
    strength     TEXT,
    aroma        TEXT,
    taste        TEXT,
    food_pairing TEXT,
    description  TEXT,
    unsaved_id   BIGINT,
    error        TEXT,
    timestamp    TIMESTAMP
);
