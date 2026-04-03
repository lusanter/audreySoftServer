-- liquibase formatted sql

-- changeset santer:03-crear-tablas-sucursales
CREATE TABLE IF NOT EXISTS core.sucursales (
    id uuid PRIMARY KEY,
    empresa_id uuid NOT NULL,
    nombre varchar(150) NOT NULL,
    direccion varchar(250),
    vertical_type varchar(50) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp,
    updated_at timestamp
);
-- rollback DROP TABLE IF EXISTS core.sucursales;

-- changeset santer:03-crear-tablas-sucursales-add-fk
ALTER TABLE core.sucursales ADD CONSTRAINT fk_sucursales_empresa FOREIGN KEY (empresa_id) REFERENCES core.empresas (id) ON DELETE CASCADE;
-- rollback ALTER TABLE core.sucursales DROP CONSTRAINT IF EXISTS fk_sucursales_empresa;
