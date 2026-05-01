-- liquibase formatted sql

-- changeset santer:29-venta-impuestos-crear-tabla
CREATE TABLE billing.venta_impuestos (
    id              uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    venta_id        uuid         NOT NULL REFERENCES billing.ventas(id) ON DELETE CASCADE,
    impuesto_id     varchar(20)  NOT NULL REFERENCES fiscal.impuesto_tipos(id),
    codigo          varchar(20)  NOT NULL,
    nombre          varchar(100) NOT NULL,
    tasa            numeric(7,4) NOT NULL,
    base            numeric(12,2) NOT NULL,
    monto           numeric(12,2) NOT NULL,
    incluido_precio boolean      NOT NULL DEFAULT true
);
-- rollback DROP TABLE IF EXISTS billing.venta_impuestos;

-- changeset santer:29-ventas-add-base-imponible-total-impuestos
ALTER TABLE billing.ventas
    ADD COLUMN base_imponible  numeric(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN total_impuestos numeric(12,2) NOT NULL DEFAULT 0;
-- rollback ALTER TABLE billing.ventas DROP COLUMN IF EXISTS base_imponible; ALTER TABLE billing.ventas DROP COLUMN IF EXISTS total_impuestos;

-- changeset santer:29-migrar-igv-a-venta-impuestos
INSERT INTO billing.venta_impuestos (venta_id, impuesto_id, codigo, nombre, tasa, base, monto, incluido_precio)
SELECT
    v.id,
    'IGV',
    'IGV',
    'Impuesto General a las Ventas',
    0.18,
    v.subtotal - v.descuento,
    v.igv,
    true
FROM billing.ventas v
WHERE v.igv > 0;
-- rollback DELETE FROM billing.venta_impuestos WHERE impuesto_id = 'IGV';

-- changeset santer:29-backfill-base-imponible-total-impuestos
UPDATE billing.ventas
SET base_imponible  = subtotal - descuento,
    total_impuestos = igv;
-- rollback UPDATE billing.ventas SET base_imponible = 0, total_impuestos = 0;

-- changeset santer:29-ventas-drop-igv
ALTER TABLE billing.ventas DROP COLUMN igv;
-- rollback ALTER TABLE billing.ventas ADD COLUMN igv numeric(12,2) NOT NULL DEFAULT 0;
