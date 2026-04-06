-- liquibase formatted sql

-- changeset santer:07-crear-esquema-inventory
CREATE SCHEMA IF NOT EXISTS inventory;
-- rollback DROP SCHEMA inventory CASCADE;

-- changeset santer:07-crear-esquema-restaurant
CREATE SCHEMA IF NOT EXISTS restaurant;
-- rollback DROP SCHEMA restaurant CASCADE;

-- changeset santer:07-crear-esquema-billing
CREATE SCHEMA IF NOT EXISTS billing;
-- rollback DROP SCHEMA billing CASCADE;
