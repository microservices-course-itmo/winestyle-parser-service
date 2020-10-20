CREATE TABLE alcohol
(
    id           BIGINT NOT NULL CONSTRAINT alcohol_pkey PRIMARY KEY,
    name         VARCHAR(115),
    type         VARCHAR(10),
    url          VARCHAR(125),
    image_url    VARCHAR(65),
    crop_year    INTEGER,
    manufacturer varchar(50),
    brand        VARCHAR(50),
    price        REAL,
    volume       REAL,
    rating       REAL,
    country      VARCHAR(15),
    region       VARCHAR(55),
    color        VARCHAR(15),
    grape        VARCHAR(100),
    sugar        VARCHAR(50),
    strength     VARCHAR(30),
    aroma        TEXT,
    taste        TEXT,
    food_pairing TEXT,
    description  TEXT
);