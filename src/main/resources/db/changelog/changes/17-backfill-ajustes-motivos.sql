-- liquibase formatted sql

-- changeset santer:17-backfill-ajustes-motivos
-- Insertar motivos predeterminados para todas las sucursales existentes que no los tengan
INSERT INTO inventory.ajuste_motivos (id, sucursal_id, nombre, tipo, active, created_at)
SELECT gen_random_uuid(), s.id, m.nombre, m.tipo, true, now()
FROM core.sucursales s
CROSS JOIN (
    VALUES 
        ('Merma / Vencimiento', 'DECREMENTO'),
        ('Consumo Interno', 'DECREMENTO'),
        ('Robo / Pérdida', 'DECREMENTO'),
        ('Sobrante de Inventario', 'INCREMENTO'),
        ('Ajuste por Auditoría', 'DECREMENTO')
) AS m(nombre, tipo)
WHERE NOT EXISTS (
    SELECT 1 FROM inventory.ajuste_motivos am 
    WHERE am.sucursal_id = s.id
);
-- rollback DELETE FROM inventory.ajuste_motivos WHERE created_at > '2026-04-05' AND nombre IN ('Merma / Vencimiento', 'Consumo Interno', 'Robo / Pérdida', 'Sobrante de Inventario', 'Ajuste por Auditoría');
