-- liquibase formatted sql

-- changeset santer:27-fiscal-crear-esquema
CREATE SCHEMA IF NOT EXISTS fiscal;
-- rollback DROP SCHEMA IF EXISTS fiscal CASCADE;

-- changeset santer:27-fiscal-sistemas
CREATE TABLE fiscal.fiscal_sistemas (
    id                   varchar(20) PRIMARY KEY,
    nombre               varchar(100) NOT NULL,
    pais_codigo          varchar(3)   NOT NULL,
    moneda_default       varchar(3)   NOT NULL,
    serie_formato        varchar(50),
    serie_regex          varchar(100),
    correlativo_padding  int          NOT NULL DEFAULT 8,
    separador            varchar(5)   NOT NULL DEFAULT '-',
    activo               boolean      NOT NULL DEFAULT true
);
-- rollback DROP TABLE IF EXISTS fiscal.fiscal_sistemas;

-- changeset santer:27-fiscal-sistemas-seed
INSERT INTO fiscal.fiscal_sistemas (id, nombre, pais_codigo, moneda_default, serie_formato, serie_regex, correlativo_padding, separador, activo) VALUES
  ('SUNAT',   'SUNAT - Perú',              'PER', 'PEN', '[A-Z][0-9]{3}',     '[A-Z][0-9]{3}',     8,  '-', true),
  ('SRI',     'SRI - Ecuador',             'ECU', 'USD', '[0-9]{3}-[0-9]{3}', '[0-9]{3}-[0-9]{3}', 9,  '-', true),
  ('DIAN',    'DIAN - Colombia',           'COL', 'COP', NULL,                NULL,                10,  '-', true),
  ('SAT',     'SAT - México',              'MEX', 'MXN', NULL,                NULL,                 8,  '-', true),
  ('INTERNO', 'Interno (sin ente fiscal)', '---', '---', NULL,                NULL,                 8,  '-', true);
-- rollback DELETE FROM fiscal.fiscal_sistemas WHERE id IN ('SUNAT','SRI','DIAN','SAT','INTERNO');

-- changeset santer:27-fiscal-impuesto-tipos
CREATE TABLE fiscal.impuesto_tipos (
    id                varchar(20) PRIMARY KEY,
    fiscal_sistema_id varchar(20)  NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    codigo            varchar(20)  NOT NULL,
    nombre            varchar(100) NOT NULL,
    descripcion       text,
    tasa_default      numeric(7,4) NOT NULL,
    tipo_calculo      varchar(20)  NOT NULL DEFAULT 'PORCENTAJE',
    activo            boolean      NOT NULL DEFAULT true
);
-- rollback DROP TABLE IF EXISTS fiscal.impuesto_tipos;

-- changeset santer:27-fiscal-impuesto-tipos-seed
INSERT INTO fiscal.impuesto_tipos (id, fiscal_sistema_id, codigo, nombre, descripcion, tasa_default, tipo_calculo, activo) VALUES
  ('IGV',    'SUNAT', 'IGV', 'Impuesto General a las Ventas', NULL, 0.1800, 'PORCENTAJE', true),
  ('ISC',    'SUNAT', 'ISC', 'Impuesto Selectivo al Consumo', NULL, 0.1700, 'PORCENTAJE', true),
  ('IVA_EC', 'SRI',   'IVA', 'Impuesto al Valor Agregado',    NULL, 0.1200, 'PORCENTAJE', true),
  ('IVA_CO', 'DIAN',  'IVA', 'Impuesto al Valor Agregado',    NULL, 0.1900, 'PORCENTAJE', true),
  ('IVA_MX', 'SAT',   'IVA', 'Impuesto al Valor Agregado',    NULL, 0.1600, 'PORCENTAJE', true);
-- rollback DELETE FROM fiscal.impuesto_tipos WHERE id IN ('IGV','ISC','IVA_EC','IVA_CO','IVA_MX');

-- changeset santer:27-fiscal-comprobante-tipos
CREATE TABLE fiscal.comprobante_tipos (
    id                uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    fiscal_sistema_id varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    codigo            varchar(30) NOT NULL,
    nombre            varchar(100) NOT NULL,
    requiere_ruc      boolean     NOT NULL DEFAULT false,
    genera_igv        boolean     NOT NULL DEFAULT true,
    activo            boolean     NOT NULL DEFAULT true,
    UNIQUE (fiscal_sistema_id, codigo)
);
-- rollback DROP TABLE IF EXISTS fiscal.comprobante_tipos;

-- changeset santer:27-fiscal-comprobante-tipos-seed
-- Seed SUNAT
INSERT INTO fiscal.comprobante_tipos (fiscal_sistema_id, codigo, nombre, requiere_ruc, genera_igv) VALUES
  ('SUNAT', 'NOTA_VENTA', 'Nota de Venta',   false, false),
  ('SUNAT', 'BOLETA',     'Boleta de Venta', false, true),
  ('SUNAT', 'FACTURA',    'Factura',          true,  true);
-- Seed SRI
INSERT INTO fiscal.comprobante_tipos (fiscal_sistema_id, codigo, nombre, requiere_ruc, genera_igv) VALUES
  ('SRI', 'FACTURA',      'Factura',          true,  true),
  ('SRI', 'NOTA_CREDITO', 'Nota de Crédito',  true,  false),
  ('SRI', 'TICKET',       'Ticket',           false, false);
-- rollback DELETE FROM fiscal.comprobante_tipos WHERE fiscal_sistema_id IN ('SUNAT','SRI');
