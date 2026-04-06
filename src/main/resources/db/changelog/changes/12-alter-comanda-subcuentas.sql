-- liquibase formatted sql

-- changeset santer:12-comanda-subcuenta-label
-- Agrega etiqueta de subcuenta a cada item para soportar "dividir cuenta"
-- sub_cuenta es un label libre: 'A', 'B', 'C' o null (sin dividir)
ALTER TABLE restaurant.comanda_items ADD COLUMN IF NOT EXISTS sub_cuenta varchar(10) DEFAULT NULL;
-- rollback ALTER TABLE restaurant.comanda_items DROP COLUMN IF EXISTS sub_cuenta;
