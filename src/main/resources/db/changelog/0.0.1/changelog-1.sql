--liquibase formatted sql

--changeset konstantin:1
--comment first migration

CREATE TABLE wallet
(
    id            UUID                                  NOT NULL,
    type          VARCHAR(255)                          NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW')),
    amount        DECIMAL(18, 2)                        NOT NULL,
    CONSTRAINT wallet_pk                                PRIMARY KEY (id)
)
