-- liquibase formatted sql

-- changeset santer:28-fiscal-config-tabla
CREATE TABLE fiscal.fiscal_config (
    sucursal_id               uuid          PRIMARY KEY REFERENCES core.sucursales(id) ON DELETE CASCADE,
    fiscal_sistema_id         varchar(20)   NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    moneda_codigo             varchar(3)    NOT NULL DEFAULT 'PEN',
    ruc_empresa               varchar(20),
    razon_social              varchar(200),
    direccion_fiscal          varchar(300),
    impuestos_default         varchar(20)[] NOT NULL DEFAULT ARRAY['IGV'],
    precios_incluyen_impuesto boolean       NOT NULL DEFAULT true,
    created_at                timestamp     NOT NULL DEFAULT now(),
    updated_at                timestamp
);
-- rollback DROP TABLE IF EXISTS fiscal.fiscal_config;

-- changeset santer:28-sucursales-add-pais-moneda
ALTER TABLE core.sucursales
    ADD COLUMN pais_codigo   varchar(3) NOT NULL DEFAULT 'PER',
    ADD COLUMN moneda_codigo varchar(3) NOT NULL DEFAULT 'PEN';
-- rollback ALTER TABLE core.sucursales DROP COLUMN IF EXISTS pais_codigo; ALTER TABLE core.sucursales DROP COLUMN IF EXISTS moneda_codigo;

-- changeset santer:28-fiscal-config-seed
INSERT INTO fiscal.fiscal_config (sucursal_id, fiscal_sistema_id, moneda_codigo, impuestos_default, precios_incluyen_impuesto)
SELECT id, 'SUNAT', 'PEN', ARRAY['IGV'], true
FROM core.sucursales;
-- rollback DELETE FROM fiscal.fiscal_config;
