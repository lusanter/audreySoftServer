-- liquibase formatted sql

-- changeset santer:30-fiscal-venta-fiscal-crear-tabla
CREATE TABLE fiscal.venta_fiscal (
    venta_id            uuid        PRIMARY KEY REFERENCES billing.ventas(id) ON DELETE CASCADE,
    fiscal_sistema_id   varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    enviado             boolean     NOT NULL DEFAULT false,
    enviado_at          timestamp,
    aceptado            boolean,
    aceptado_at         timestamp,
    codigo_respuesta    varchar(50),
    mensaje_respuesta   text,
    xml_firmado         text,
    extra               jsonb
);
-- rollback DROP TABLE IF EXISTS fiscal.venta_fiscal;

-- changeset santer:30-migrar-sunat-a-venta-fiscal
INSERT INTO fiscal.venta_fiscal (venta_id, fiscal_sistema_id, enviado, aceptado, codigo_respuesta)
SELECT id, 'SUNAT', sunat_enviado, sunat_aceptado, sunat_codigo_hash
FROM billing.ventas;
-- rollback DELETE FROM fiscal.venta_fiscal WHERE fiscal_sistema_id = 'SUNAT';

-- changeset santer:30-ventas-drop-sunat-columns
ALTER TABLE billing.ventas
    DROP COLUMN sunat_enviado,
    DROP COLUMN sunat_aceptado,
    DROP COLUMN sunat_codigo_hash,
    DROP COLUMN sunat_enviado_at;
-- rollback ALTER TABLE billing.ventas ADD COLUMN sunat_enviado boolean NOT NULL DEFAULT false; ALTER TABLE billing.ventas ADD COLUMN sunat_aceptado boolean; ALTER TABLE billing.ventas ADD COLUMN sunat_codigo_hash varchar(100); ALTER TABLE billing.ventas ADD COLUMN sunat_enviado_at timestamp;

-- changeset santer:30-comprobante-series-ampliar-serie
ALTER TABLE billing.comprobante_series
    ALTER COLUMN serie TYPE varchar(20);
-- rollback ALTER TABLE billing.comprobante_series ALTER COLUMN serie TYPE varchar(4);
