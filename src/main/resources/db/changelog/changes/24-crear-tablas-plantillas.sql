-- ============================================================================
-- Migración 24: Crear tablas para plantillas de diseño
-- ============================================================================

-- Crear tabla de plantillas
CREATE TABLE design.plantillas (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    tipo_uso VARCHAR(20) NOT NULL CHECK (tipo_uso IN ('IMPULSAR', 'OFERTA', 'NUEVO_PRODUCTO', 'EVENTO')),
    formato VARCHAR(20) NOT NULL CHECK (formato IN ('NINE_SIXTEEN', 'ONE_ONE', 'SIXTEEN_NINE', 'FOUR_FIVE')),
    tags JSONB,
    origen VARCHAR(20) NOT NULL CHECK (origen IN ('SUPER_ADMIN', 'EMPRESA')),
    capas JSONB NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    empresa_id UUID,
    
    -- Clave foránea
    CONSTRAINT fk_plantillas_empresa FOREIGN KEY (empresa_id) REFERENCES core.empresas(id) ON DELETE CASCADE
);

-- Crear índices por separado (sintaxis PostgreSQL)
CREATE INDEX idx_plantillas_nombre ON design.plantillas (nombre);
CREATE INDEX idx_plantillas_tipo_uso ON design.plantillas (tipo_uso);
CREATE INDEX idx_plantillas_formato ON design.plantillas (formato);
CREATE INDEX idx_plantillas_origen ON design.plantillas (origen);
CREATE INDEX idx_plantillas_empresa_id ON design.plantillas (empresa_id);
CREATE INDEX idx_plantillas_activa ON design.plantillas (activa);
CREATE INDEX idx_plantillas_fecha_creacion ON design.plantillas (fecha_creacion);

-- Comentarios para documentación (PostgreSQL)
COMMENT ON TABLE design.plantillas IS 'Plantillas de diseño para generación de flyers';
COMMENT ON COLUMN design.plantillas.id IS 'ID único de la plantilla (UUID)';
COMMENT ON COLUMN design.plantillas.nombre IS 'Nombre único de la plantilla';
COMMENT ON COLUMN design.plantillas.descripcion IS 'Descripción de la plantilla y su uso';
COMMENT ON COLUMN design.plantillas.tipo_uso IS 'Tipo de uso: IMPULSAR, OFERTA, NUEVO_PRODUCTO, EVENTO';
COMMENT ON COLUMN design.plantillas.formato IS 'Formato de la plantilla: 9:16, 1:1, 16:9, 4:5';
COMMENT ON COLUMN design.plantillas.tags IS 'Tags para búsqueda y categorización';
COMMENT ON COLUMN design.plantillas.origen IS 'Origen: SUPER_ADMIN (global) o EMPRESA (específica)';
COMMENT ON COLUMN design.plantillas.capas IS 'Definición de capas de la plantilla en formato JSON';
COMMENT ON COLUMN design.plantillas.fecha_creacion IS 'Fecha y hora de creación';
COMMENT ON COLUMN design.plantillas.activa IS 'Indica si la plantilla está activa';
COMMENT ON COLUMN design.plantillas.empresa_id IS 'ID de la empresa (null para plantillas SUPER_ADMIN)';

-- Insertar algunas plantillas de ejemplo para SUPER_ADMIN (PostgreSQL)
INSERT INTO design.plantillas (
    id, 
    nombre, 
    descripcion, 
    tipo_uso, 
    formato, 
    tags, 
    origen, 
    capas, 
    fecha_creacion, 
    activa, 
    empresa_id
) VALUES 
(
    gen_random_uuid()::text,
    'Impulsar Producto - Stories',
    'Plantilla básica para impulsar productos en formato Stories (9:16)',
    'IMPULSAR',
    'NINE_SIXTEEN',
    '["impulsar", "producto", "stories", "basico"]'::jsonb,
    'SUPER_ADMIN',
    '[
        {
            "tipo": "BACKGROUND",
            "x": 0,
            "y": 0,
            "width": 1,
            "height": 1,
            "zIndex": 0,
            "applyCircularMask": false,
            "colorHint": null,
            "textHint": null
        },
        {
            "tipo": "PRODUCT",
            "x": 0.3,
            "y": 0.2,
            "width": 0.4,
            "height": 0.4,
            "zIndex": 1,
            "applyCircularMask": true,
            "colorHint": null,
            "textHint": null
        },
        {
            "tipo": "TEXT_TITLE",
            "x": 0.1,
            "y": 0.65,
            "width": 0.8,
            "height": 0.15,
            "zIndex": 2,
            "applyCircularMask": false,
            "colorHint": "PRINCIPAL",
            "textHint": "NOMBRE_PRODUCTO"
        },
        {
            "tipo": "TEXT_PRICE",
            "x": 0.1,
            "y": 0.82,
            "width": 0.8,
            "height": 0.1,
            "zIndex": 3,
            "applyCircularMask": false,
            "colorHint": "CONTRASTE",
            "textHint": "PRECIO"
        }
    ]'::jsonb,
    NOW(),
    true,
    null
),
(
    gen_random_uuid()::text,
    'Oferta Especial - Cuadrado',
    'Plantilla para ofertas especiales en formato cuadrado (1:1)',
    'OFERTA',
    'ONE_ONE',
    '["oferta", "especial", "cuadrado", "promocion"]'::jsonb,
    'SUPER_ADMIN',
    '[
        {
            "tipo": "BACKGROUND",
            "x": 0,
            "y": 0,
            "width": 1,
            "height": 1,
            "zIndex": 0,
            "applyCircularMask": false,
            "colorHint": null,
            "textHint": null
        },
        {
            "tipo": "OVERLAY",
            "x": 0,
            "y": 0,
            "width": 1,
            "height": 0.3,
            "zIndex": 1,
            "applyCircularMask": false,
            "colorHint": "SECUNDARIO",
            "textHint": null
        },
        {
            "tipo": "PRODUCT",
            "x": 0.25,
            "y": 0.35,
            "width": 0.5,
            "height": 0.4,
            "zIndex": 2,
            "applyCircularMask": false,
            "colorHint": null,
            "textHint": null
        },
        {
            "tipo": "TEXT_TITLE",
            "x": 0.05,
            "y": 0.05,
            "width": 0.9,
            "height": 0.2,
            "zIndex": 3,
            "applyCircularMask": false,
            "colorHint": "CONTRASTE",
            "textHint": "NOMBRE_PRODUCTO"
        },
        {
            "tipo": "TEXT_PRICE",
            "x": 0.05,
            "y": 0.8,
            "width": 0.9,
            "height": 0.15,
            "zIndex": 4,
            "applyCircularMask": false,
            "colorHint": "PRINCIPAL",
            "textHint": "PRECIO"
        }
    ]'::jsonb,
    NOW(),
    true,
    null
);