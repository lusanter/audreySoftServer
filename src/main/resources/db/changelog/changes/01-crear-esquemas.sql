-- liquibase formatted sql

-- changeset santer:01-crear-esquemas
CREATE SCHEMA IF NOT EXISTS core;
-- rollback DROP SCHEMA core CASCADE;