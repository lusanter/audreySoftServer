-- liquibase formatted sql

-- changeset santer:22-create-venta-origen
CREATE TABLE billing.venta_origen (
    venta_id    uuid        PRIMARY KEY,
    tipo_origen varchar(30) NOT NULL
        CONSTRAINT chk_venta_origen_tipo CHECK (tipo_origen IN ('COMANDA','PEDIDO_ONLINE','COTIZACION','DIRECTO')),
    origen_id   uuid        NOT NULL,
    CONSTRAINT fk_venta_origen_venta FOREIGN KEY (venta_id) REFERENCES billing.ventas(id) ON DELETE CASCADE
);
-- rollback DROP TABLE IF EXISTS billing.venta_origen;

-- changeset santer:22-migrate-comanda-id
INSERT INTO billing.venta_origen (venta_id, tipo_origen, origen_id)
SELECT id, 'COMANDA', comanda_id
FROM billing.ventas
WHERE comanda_id IS NOT NULL;
-- rollback DELETE FROM billing.venta_origen WHERE tipo_origen = 'COMANDA';

-- changeset santer:22-drop-comanda-fk-index-col splitStatements:true endDelimiter:;
ALTER TABLE billing.ventas DROP CONSTRAINT IF EXISTS fk_ventas_comanda;
DROP INDEX IF EXISTS idx_ventas_comanda;
ALTER TABLE billing.ventas DROP COLUMN IF EXISTS comanda_id;
-- rollback ALTER TABLE billing.ventas ADD COLUMN IF NOT EXISTS comanda_id uuid; ALTER TABLE billing.ventas ADD CONSTRAINT fk_ventas_comanda FOREIGN KEY (comanda_id) REFERENCES restaurant.comandas (id) ON DELETE SET NULL; CREATE INDEX IF NOT EXISTS idx_ventas_comanda ON billing.ventas (comanda_id);

-- changeset santer:22-create-origen-index
CREATE INDEX idx_venta_origen_origen_id ON billing.venta_origen (origen_id);
-- rollback DROP INDEX IF EXISTS idx_venta_origen_origen_id;
