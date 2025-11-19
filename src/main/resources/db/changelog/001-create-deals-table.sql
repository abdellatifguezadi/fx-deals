--liquibase formatted sql

--changeset abdellatif:001
CREATE TABLE deals (
    deal_unique_id VARCHAR(255) PRIMARY KEY,
    from_currency_iso_code VARCHAR(3) NOT NULL,
    to_currency_iso_code VARCHAR(3) NOT NULL,
    deal_timestamp DATETIME NOT NULL,
    deal_amount DECIMAL(17,2) NOT NULL,
    created_at DATETIME NOT NULL
);