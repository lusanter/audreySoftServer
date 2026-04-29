-- liquibase formatted sql

-- changeset santer:20-ventas-add-columns splitStatements:true endDelimiter:;
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS comprobante_serie_id uuid;
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS estado               varchar(20) NOT NULL DEFAULT 'COBRADA';
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS sunat_enviado        boolean     NOT NULL DEFAULT false;
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS sunat_aceptado       boolean;
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS sunat_codigo_hash    varchar(100);
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS sunat_enviado_at     timestamp;
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS anulada_at           timestamp;
-- rollback SELECT 1;

-- changeset santer:20-ventas-fk-serie
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.table_constraints WHERE constraint_name='fk_ventas_serie' AND table_schema='billing'
ALTER TABLE billing.ventas ADD CONSTRAINT fk_ventas_serie FOREIGN KEY (comprobante_serie_id) REFERENCES billing.comprobante_series (id) ON DELETE SET NULL;
-- rollback ALTER TABLE billing.ventas DROP CONSTRAINT IF EXISTS fk_ventas_serie;
