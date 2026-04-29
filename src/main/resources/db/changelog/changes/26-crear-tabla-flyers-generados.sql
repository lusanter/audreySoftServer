--liquibase formatted sql

--changeset audrey:26-crear-tabla-flyers-generados
--comment: Tabla para persistir el historial de flyers generados por empresa/usuario
CREATE TABLE IF NOT EXISTS design.flyers_generados (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id    UUID NOT NULL,
    usuario_id    UUID NOT NULL,
    producto_ids  JSONB NOT NULL DEFAULT '[]',
    tipo_uso      TEXT NOT NULL,
    formato       TEXT NOT NULL,
    palette       JSONB NOT NULL DEFAULT '{}',
    ai_flyer_dto  JSONB NOT NULL DEFAULT '{}',
    plantilla_id  UUID,
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);

--changeset audrey:26-index-flyers-empresa-created
--comment: Índice compuesto para listar historial por empresa ordenado por fecha
CREATE INDEX IF NOT EXISTS idx_flyers_empresa_created
    ON design.flyers_generados (empresa_id, created_at DESC);

--rollback DROP TABLE IF EXISTS design.flyers_generados;
