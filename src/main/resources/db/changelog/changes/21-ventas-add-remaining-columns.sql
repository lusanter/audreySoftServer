-- liquibase formatted sql

-- changeset santer:21-ventas-add-remaining-columns splitStatements:true endDelimiter:;
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS tipo_comprobante   varchar(20) NOT NULL DEFAULT 'NOTA_VENTA';
ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS numero_comprobante varchar(20);
-- rollback SELECT 1;
