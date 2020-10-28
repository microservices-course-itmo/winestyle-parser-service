CREATE TABLE IF NOT EXISTS image
(
    image      BYTEA,
    alcohol_id INT NOT NULL CONSTRAINT image_pkey PRIMARY KEY
);