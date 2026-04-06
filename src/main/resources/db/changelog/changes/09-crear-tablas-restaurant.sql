-- liquibase formatted sql

-- changeset santer:09-restaurant-mesas
CREATE TABLE IF NOT EXISTS restaurant.mesas (
    id          uuid PRIMARY KEY,
    sucursal_id uuid NOT NULL,
    numero      int NOT NULL,
    capacidad   int NOT NULL DEFAULT 4,
    estado      varchar(20) NOT NULL DEFAULT 'LIBRE',   -- LIBRE | OCUPADA | RESERVADA
    active      boolean NOT NULL DEFAULT true,
    created_at  timestamp,
    updated_at  timestamp,
    CONSTRAINT fk_mesas_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE,
    CONSTRAINT uq_mesa_numero_sucursal UNIQUE (sucursal_id, numero)
);
-- rollback DROP TABLE IF EXISTS restaurant.mesas;

-- changeset santer:09-restaurant-comandas
CREATE TABLE IF NOT EXISTS restaurant.comandas (
    id          uuid PRIMARY KEY,
    sucursal_id uuid NOT NULL,
    mesa_id     uuid,
    cliente_id  uuid,
    estado      varchar(20) NOT NULL DEFAULT 'ABIERTA',  -- ABIERTA | EN_COCINA | LISTA | CERRADA | CANCELADA
    total       numeric(12, 2) NOT NULL DEFAULT 0,
    notas       text,
    created_at  timestamp NOT NULL,
    closed_at   timestamp,
    CONSTRAINT fk_comandas_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE,
    CONSTRAINT fk_comandas_mesa     FOREIGN KEY (mesa_id)     REFERENCES restaurant.mesas (id) ON DELETE SET NULL,
    CONSTRAINT fk_comandas_cliente  FOREIGN KEY (cliente_id)  REFERENCES inventory.clientes (id) ON DELETE SET NULL
);
-- rollback DROP TABLE IF EXISTS restaurant.comandas;

-- changeset santer:09-restaurant-comanda-items
CREATE TABLE IF NOT EXISTS restaurant.comanda_items (
    id              uuid PRIMARY KEY,
    comanda_id      uuid NOT NULL,
    producto_id     uuid NOT NULL,
    cantidad        numeric(12, 3) NOT NULL DEFAULT 1,
    precio_unitario numeric(12, 2) NOT NULL,
    subtotal        numeric(12, 2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    notas           varchar(255),
    estado          varchar(20) NOT NULL DEFAULT 'PENDIENTE',  -- PENDIENTE | EN_PREPARACION | LISTO | ENTREGADO | CANCELADO
    created_at      timestamp NOT NULL,
    updated_at      timestamp,
    CONSTRAINT fk_comanda_items_comanda  FOREIGN KEY (comanda_id)  REFERENCES restaurant.comandas (id) ON DELETE CASCADE,
    CONSTRAINT fk_comanda_items_producto FOREIGN KEY (producto_id) REFERENCES inventory.productos (id)
);
-- rollback DROP TABLE IF EXISTS restaurant.comanda_items;

-- changeset santer:09-restaurant-indices
CREATE INDEX IF NOT EXISTS idx_mesas_sucursal      ON restaurant.mesas (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_comandas_sucursal   ON restaurant.comandas (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_comandas_mesa       ON restaurant.comandas (mesa_id);
CREATE INDEX IF NOT EXISTS idx_comanda_items_cmd   ON restaurant.comanda_items (comanda_id);
-- rollback DROP INDEX IF EXISTS idx_mesas_sucursal; DROP INDEX IF EXISTS idx_comandas_sucursal; DROP INDEX IF EXISTS idx_comandas_mesa; DROP INDEX IF EXISTS idx_comanda_items_cmd;
