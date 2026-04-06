-- liquibase formatted sql

-- changeset santer:15-inventory-ajuste-motivos
CREATE TABLE IF NOT EXISTS inventory.ajuste_motivos (
    id          uuid PRIMARY KEY,
    sucursal_id uuid NOT NULL,
    nombre      varchar(100) NOT NULL,
    tipo        varchar(20) NOT NULL,   -- INCREMENTO | DECREMENTO
    active      boolean NOT NULL DEFAULT true,
    created_at  timestamp,
    updated_at  timestamp,
    CONSTRAINT fk_ajuste_motivos_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE
);
-- rollback DROP TABLE IF EXISTS inventory.ajuste_motivos;

-- changeset santer:15-stock-movements-add-motivo
ALTER TABLE inventory.stock_movements ADD COLUMN IF NOT EXISTS ajuste_motivo_id uuid;
ALTER TABLE inventory.stock_movements ADD CONSTRAINT fk_stock_movements_motivo FOREIGN KEY (ajuste_motivo_id) REFERENCES inventory.ajuste_motivos (id) ON DELETE SET NULL;
-- rollback ALTER TABLE inventory.stock_movements DROP CONSTRAINT fk_stock_movements_motivo; ALTER TABLE inventory.stock_movements DROP COLUMN IF EXISTS ajuste_motivo_id;

-- changeset santer:15-trigger-ajustes-default splitStatements:false
CREATE OR REPLACE FUNCTION inventory.fn_crear_ajustes_default()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO inventory.ajuste_motivos (id, sucursal_id, nombre, tipo, active, created_at)
    VALUES 
        (gen_random_uuid(), NEW.id, 'Merma / Vencimiento', 'DECREMENTO', true, now()),
        (gen_random_uuid(), NEW.id, 'Consumo Interno', 'DECREMENTO', true, now()),
        (gen_random_uuid(), NEW.id, 'Robo / Pérdida', 'DECREMENTO', true, now()),
        (gen_random_uuid(), NEW.id, 'Sobrante de Inventario', 'INCREMENTO', true, now()),
        (gen_random_uuid(), NEW.id, 'Ajuste por Auditoría', 'DECREMENTO', true, now());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tr_crear_ajustes_default ON core.sucursales;
CREATE TRIGGER tr_crear_ajustes_default
AFTER INSERT ON core.sucursales
FOR EACH ROW
EXECUTE FUNCTION inventory.fn_crear_ajustes_default();
-- rollback DROP TRIGGER IF EXISTS tr_crear_ajustes_default ON core.sucursales; DROP FUNCTION IF EXISTS inventory.fn_crear_ajustes_default();
