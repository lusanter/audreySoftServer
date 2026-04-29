-- liquibase formatted sql

-- changeset santer:23-productos-add-imagen-url
ALTER TABLE inventory.productos ADD COLUMN IF NOT EXISTS imagen_url varchar(500);
-- rollback ALTER TABLE inventory.productos DROP COLUMN IF EXISTS imagen_url;

-- changeset santer:23-sucursales-add-imagen-url
ALTER TABLE core.sucursales ADD COLUMN IF NOT EXISTS imagen_url varchar(500);
-- rollback ALTER TABLE core.sucursales DROP COLUMN IF EXISTS imagen_url;
