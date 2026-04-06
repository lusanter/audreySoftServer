-- liquibase formatted sql

-- changeset santer:11-seed-metodos-cobro
-- Métodos de cobro globales (sucursal_id NULL = disponibles en todas las sucursales)
INSERT INTO billing.metodos_cobro (id, sucursal_id, nombre, codigo, activo, created_at) VALUES
    ('a1000000-0000-0000-0000-000000000001', NULL, 'Efectivo',      'EFECTIVO',      true, NOW()),
    ('a1000000-0000-0000-0000-000000000002', NULL, 'Yape',          'YAPE',          true, NOW()),
    ('a1000000-0000-0000-0000-000000000003', NULL, 'Plin',          'PLIN',          true, NOW()),
    ('a1000000-0000-0000-0000-000000000004', NULL, 'Transferencia', 'TRANSFERENCIA', true, NOW())
ON CONFLICT (id) DO NOTHING;
-- rollback DELETE FROM billing.metodos_cobro WHERE id IN ('a1000000-0000-0000-0000-000000000001','a1000000-0000-0000-0000-000000000002','a1000000-0000-0000-0000-000000000003','a1000000-0000-0000-0000-000000000004');
