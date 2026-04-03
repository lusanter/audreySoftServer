-- liquibase formatted sql

-- changeset santer:02-crear-tablas-empresas
CREATE TABLE IF NOT EXISTS core.empresas (
    id uuid PRIMARY KEY,
    nombre varchar(150) NOT NULL,
    ruc varchar(20) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp,
    updated_at timestamp
);
-- rollback DROP TABLE IF EXISTS core.empresas;

-- changeset santer:02-crear-tablas-empresas-add-constraints
ALTER TABLE core.empresas ADD CONSTRAINT uk_empresas_ruc UNIQUE (ruc);
-- rollback ALTER TABLE core.empresas DROP CONSTRAINT IF EXISTS uk_empresas_ruc;
