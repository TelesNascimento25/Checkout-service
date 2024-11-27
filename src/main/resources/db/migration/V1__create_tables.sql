CREATE TYPE basket_status AS ENUM ('OPEN', 'CHECKED_OUT', 'CANCELLED', 'DELETED');

CREATE CAST ( VARCHAR AS basket_status ) WITH INOUT AS IMPLICIT;

CREATE TABLE baskets
(
    id         SERIAL PRIMARY KEY,
    status     basket_status  NOT NULL,
    total      DECIMAL(10, 2),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE basket_items
(
    id         SERIAL PRIMARY KEY,
    basket_id  INT     NOT NULL REFERENCES baskets (id),
    product_id VARCHAR NOT NULL,
    quantity   INT     NOT NULL
);
