-- liquibase formatted sql

-- changeset santer:08-inventory-categorias
CREATE TABLE IF NOT EXISTS inventory.categorias (
    id          uuid PRIMARY KEY,
    sucursal_id uuid NOT NULL,
    nombre      varchar(100) NOT NULL,
    active      boolean NOT NULL DEFAULT true,
    created_at  timestamp,
    updated_at  timestamp,
    CONSTRAINT fk_categorias_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE
);
-- rollback DROP TABLE IF EXISTS inventory.categorias;

-- changeset santer:08-inventory-productos
CREATE TABLE IF NOT EXISTS inventory.productos (
    id            uuid PRIMARY KEY,
    sucursal_id   uuid NOT NULL,
    categoria_id  uuid,
    nombre        varchar(150) NOT NULL,
    descripcion   text,
    precio        numeric(12, 2) NOT NULL DEFAULT 0,
    stock_actual  numeric(12, 3) NOT NULL DEFAULT 0,
    stock_minimo  numeric(12, 3) NOT NULL DEFAULT 0,
    unidad        varchar(20) NOT NULL DEFAULT 'UND',
    active        boolean NOT NULL DEFAULT true,
    created_at    timestamp,
    updated_at    timestamp,
    CONSTRAINT fk_productos_sucursal  FOREIGN KEY (sucursal_id)  REFERENCES core.sucursales (id) ON DELETE CASCADE,
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES inventory.categorias (id) ON DELETE SET NULL
);
-- rollback DROP TABLE IF EXISTS inventory.productos;

-- changeset santer:08-inventory-clientes
CREATE TABLE IF NOT EXISTS inventory.clientes (
    id          uuid PRIMARY KEY,
    sucursal_id uuid NOT NULL,
    nombre      varchar(150) NOT NULL,
    documento   varchar(20),
    email       varchar(100),
    telefono    varchar(20),
    active      boolean NOT NULL DEFAULT true,
    created_at  timestamp,
    updated_at  timestamp,
    CONSTRAINT fk_clientes_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE
);
-- rollback DROP TABLE IF EXISTS inventory.clientes;

-- changeset santer:08-inventory-stock-movements
CREATE TABLE IF NOT EXISTS inventory.stock_movements (
    id             uuid PRIMARY KEY,
    producto_id    uuid NOT NULL,
    tipo           varchar(20) NOT NULL,   -- ENTRADA | SALIDA | AJUSTE
    cantidad       numeric(12, 3) NOT NULL,
    referencia_id  uuid,                   -- comanda_item_id o null si es ajuste manual
    nota           varchar(255),
    created_at     timestamp NOT NULL,
    CONSTRAINT fk_stock_movements_producto FOREIGN KEY (producto_id) REFERENCES inventory.productos (id) ON DELETE CASCADE
);
-- rollback DROP TABLE IF EXISTS inventory.stock_movements;

-- changeset santer:08-inventory-indices
CREATE INDEX IF NOT EXISTS idx_productos_sucursal   ON inventory.productos (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_categorias_sucursal  ON inventory.categorias (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_clientes_sucursal    ON inventory.clientes (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_stock_mov_producto   ON inventory.stock_movements (producto_id);
-- rollback DROP INDEX IF EXISTS idx_productos_sucursal; DROP INDEX IF EXISTS idx_categorias_sucursal; DROP INDEX IF EXISTS idx_clientes_sucursal; DROP INDEX IF EXISTS idx_stock_mov_producto;

-- changeset santer:08-inventory-productos-control-stock
ALTER TABLE inventory.productos ADD COLUMN IF NOT EXISTS control_stock boolean NOT NULL DEFAULT true;
-- rollback ALTER TABLE inventory.productos DROP COLUMN IF EXISTS control_stock;

-- changeset santer:08-inventory-productos-precio-costo
ALTER TABLE inventory.productos ADD COLUMN IF NOT EXISTS precio_costo numeric(12,2) NOT NULL DEFAULT 0;
-- rollback ALTER TABLE inventory.productos DROP COLUMN IF EXISTS precio_costo;

-- changeset santer:08-inventory-stock-movements-precio-costo
ALTER TABLE inventory.stock_movements ADD COLUMN IF NOT EXISTS precio_costo numeric(12,2);
-- rollback ALTER TABLE inventory.stock_movements DROP COLUMN IF EXISTS precio_costo;
