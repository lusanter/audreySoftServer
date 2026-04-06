-- liquibase formatted sql

-- changeset santer:10-billing-metodos-cobro
-- Catálogo de métodos de cobro: global (sucursal_id NULL) o por sucursal
CREATE TABLE IF NOT EXISTS billing.metodos_cobro (
    id          uuid PRIMARY KEY,
    sucursal_id uuid,                        -- NULL = disponible en todas las sucursales
    nombre      varchar(80) NOT NULL,
    codigo      varchar(30) NOT NULL,        -- EFECTIVO | YAPE | PLIN | TRANSFERENCIA | etc.
    activo      boolean NOT NULL DEFAULT true,
    created_at  timestamp,
    CONSTRAINT fk_metodos_cobro_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE,
    CONSTRAINT uq_metodos_cobro_codigo_sucursal UNIQUE (codigo, sucursal_id)
);
-- rollback DROP TABLE IF EXISTS billing.metodos_cobro;

-- changeset santer:10-billing-comprobante-series
-- Configuración de series por sucursal según SUNAT Perú.
-- Cada fila representa una serie activa (ej: B001, F001, NV01).
-- El correlativo_actual se incrementa atómicamente con cada venta emitida.
-- Cuando correlativo_actual = correlativo_max se debe crear una nueva serie (B002, F002...).
CREATE TABLE IF NOT EXISTS billing.comprobante_series (
    id                  uuid PRIMARY KEY,
    sucursal_id         uuid NOT NULL,
    tipo_comprobante    varchar(20) NOT NULL,    -- BOLETA | FACTURA | NOTA_VENTA
    serie               varchar(4)  NOT NULL,    -- B001, F001, NV01 (formato SUNAT: 1 letra + 3 dígitos)
    correlativo_actual  int         NOT NULL DEFAULT 0,
    correlativo_max     int         NOT NULL DEFAULT 99999999,
    activo              boolean     NOT NULL DEFAULT true,
    created_at          timestamp   NOT NULL,
    updated_at          timestamp,
    CONSTRAINT fk_comprobante_series_sucursal FOREIGN KEY (sucursal_id) REFERENCES core.sucursales (id) ON DELETE CASCADE,
    CONSTRAINT uq_serie_sucursal UNIQUE (sucursal_id, serie)            -- no puede haber dos B001 en la misma sucursal
);
-- rollback DROP TABLE IF EXISTS billing.comprobante_series;

-- changeset santer:10-billing-ventas
-- Cabecera fiscal: 1 venta por comanda cerrada.
-- numero_comprobante = serie + '-' + correlativo con padding de 8 dígitos (ej: B001-00000001)
CREATE TABLE IF NOT EXISTS billing.ventas (
    id                   uuid        PRIMARY KEY,
    sucursal_id          uuid        NOT NULL,
    comprobante_serie_id uuid,                   -- FK a comprobante_series (NULL = nota de venta interna sin SUNAT)
    comanda_id           uuid,                   -- NULL si es venta directa retail (sin comanda)
    cliente_id           uuid,
    tipo_comprobante     varchar(20) NOT NULL DEFAULT 'NOTA_VENTA',  -- BOLETA | FACTURA | NOTA_VENTA
    serie                varchar(4),             -- snapshot: B001, F001, NV01
    correlativo          int,                    -- snapshot: número asignado al momento de emitir
    numero_comprobante   varchar(20),            -- snapshot calculado: B001-00000001 (para mostrar/imprimir)
    subtotal             numeric(12, 2) NOT NULL DEFAULT 0,
    descuento            numeric(12, 2) NOT NULL DEFAULT 0,
    igv                  numeric(12, 2) NOT NULL DEFAULT 0,  -- 18% sobre subtotal - descuento
    total                numeric(12, 2) NOT NULL DEFAULT 0,
    estado               varchar(20) NOT NULL DEFAULT 'COBRADA',  -- COBRADA | ANULADA
    -- campos para futura integración OSE/SUNAT
    sunat_enviado        boolean     NOT NULL DEFAULT false,
    sunat_aceptado       boolean,
    sunat_codigo_hash    varchar(100),           -- hash CDR de respuesta SUNAT
    sunat_enviado_at     timestamp,
    created_at           timestamp   NOT NULL,
    anulada_at           timestamp,
    CONSTRAINT fk_ventas_sucursal          FOREIGN KEY (sucursal_id)          REFERENCES core.sucursales (id) ON DELETE CASCADE,
    CONSTRAINT fk_ventas_serie             FOREIGN KEY (comprobante_serie_id) REFERENCES billing.comprobante_series (id) ON DELETE SET NULL,
    CONSTRAINT fk_ventas_comanda           FOREIGN KEY (comanda_id)           REFERENCES restaurant.comandas (id) ON DELETE SET NULL,
    CONSTRAINT fk_ventas_cliente           FOREIGN KEY (cliente_id)           REFERENCES inventory.clientes (id) ON DELETE SET NULL,
    CONSTRAINT uq_ventas_serie_correlativo UNIQUE (sucursal_id, serie, correlativo)  -- no puede repetirse B001-00000001 en la misma sucursal
);
-- rollback DROP TABLE IF EXISTS billing.ventas;

-- changeset santer:10-billing-venta-items
-- Detalle fiscal inmutable: snapshot de lo vendido al momento del cierre.
-- nombre_producto y precio_unitario son snapshots: no cambian si editan el catálogo.
CREATE TABLE IF NOT EXISTS billing.venta_items (
    id              uuid PRIMARY KEY,
    venta_id        uuid NOT NULL,
    producto_id     uuid,                        -- NULL si el producto fue eliminado del catálogo
    nombre_producto varchar(150) NOT NULL,        -- snapshot
    cantidad        numeric(12, 3) NOT NULL,
    precio_unitario numeric(12, 2) NOT NULL,      -- snapshot
    descuento_item  numeric(12, 2) NOT NULL DEFAULT 0,
    subtotal        numeric(12, 2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    CONSTRAINT fk_venta_items_venta    FOREIGN KEY (venta_id)    REFERENCES billing.ventas (id) ON DELETE CASCADE,
    CONSTRAINT fk_venta_items_producto FOREIGN KEY (producto_id) REFERENCES inventory.productos (id) ON DELETE SET NULL
);
-- rollback DROP TABLE IF EXISTS billing.venta_items;

-- changeset santer:10-billing-venta-cobros
-- Split cobro: N métodos de cobro por venta (ej: S/50 efectivo + S/30 Yape)
CREATE TABLE IF NOT EXISTS billing.venta_cobros (
    id                uuid PRIMARY KEY,
    venta_id          uuid NOT NULL,
    metodo_cobro_id   uuid NOT NULL,
    monto             numeric(12, 2) NOT NULL,
    referencia        varchar(100),              -- número de operación Yape/Plin/transferencia
    created_at        timestamp NOT NULL,
    CONSTRAINT fk_venta_cobros_venta         FOREIGN KEY (venta_id)         REFERENCES billing.ventas (id) ON DELETE CASCADE,
    CONSTRAINT fk_venta_cobros_metodo_cobro  FOREIGN KEY (metodo_cobro_id)  REFERENCES billing.metodos_cobro (id)
);
-- rollback DROP TABLE IF EXISTS billing.venta_cobros;

-- changeset santer:10-billing-indices
CREATE INDEX IF NOT EXISTS idx_ventas_sucursal          ON billing.ventas (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_ventas_comanda           ON billing.ventas (comanda_id);
CREATE INDEX IF NOT EXISTS idx_ventas_created_at        ON billing.ventas (created_at);
CREATE INDEX IF NOT EXISTS idx_ventas_sunat_enviado     ON billing.ventas (sunat_enviado) WHERE sunat_enviado = false;
CREATE INDEX IF NOT EXISTS idx_venta_items_venta        ON billing.venta_items (venta_id);
CREATE INDEX IF NOT EXISTS idx_venta_cobros_venta       ON billing.venta_cobros (venta_id);
CREATE INDEX IF NOT EXISTS idx_comprobante_series_suc   ON billing.comprobante_series (sucursal_id);
-- rollback DROP INDEX IF EXISTS idx_ventas_sucursal; DROP INDEX IF EXISTS idx_ventas_comanda; DROP INDEX IF EXISTS idx_ventas_created_at; DROP INDEX IF EXISTS idx_ventas_sunat_enviado; DROP INDEX IF EXISTS idx_venta_items_venta; DROP INDEX IF EXISTS idx_venta_cobros_venta; DROP INDEX IF EXISTS idx_comprobante_series_suc;
