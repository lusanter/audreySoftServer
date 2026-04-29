-- ============================================================================
-- Migración 25: Corregir tipos UUID en tabla design.plantillas
-- ============================================================================

-- Cambiar columna id de VARCHAR(36) a UUID nativo
ALTER TABLE design.plantillas
    ALTER COLUMN id TYPE UUID USING id::uuid;

-- Cambiar columna empresa_id de VARCHAR(36) a UUID nativo (ya era UUID pero por si acaso)
ALTER TABLE design.plantillas
    ALTER COLUMN empresa_id TYPE UUID USING empresa_id::uuid;
