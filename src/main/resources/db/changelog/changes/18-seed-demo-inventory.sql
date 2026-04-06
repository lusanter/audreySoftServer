-- liquibase formatted sql

-- changeset santer:18-seed-demo-company-branch
-- Crear empresa y sucursal base para el entorno de desarrollo/demo
INSERT INTO core.empresas (id, nombre, ruc, active, created_at, updated_at)
VALUES ('7fbc1aa0-4885-4fab-8bc2-19d75e10c5cc', 'Audrey Software Dev', '20601234567', true, now(), now())
ON CONFLICT (ruc) DO NOTHING;

INSERT INTO core.sucursales (id, empresa_id, nombre, direccion, vertical_type, active, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', '7fbc1aa0-4885-4fab-8bc2-19d75e10c5cc', 'Sede Central (Demo)', 'Av. Principal 123, Lima', 'RETAIL', true, now(), now())
ON CONFLICT (id) DO NOTHING;

-- changeset santer:18-seed-demo-user-assignment
-- Vincular el usuario admin a la sucursal demo
INSERT INTO core.user_role_assignments (id, user_id, role_type, scope_type, scope_id, active, created_at)
SELECT gen_random_uuid(), u.id, 'ADMIN', 'SUCURSAL', '00000000-0000-0000-0000-000000000000', true, now()
FROM core.usuarios u
WHERE u.username = 'admin'
ON CONFLICT DO NOTHING;

-- changeset santer:18-seed-demo-inventory-data
-- Crear categorías y productos de ejemplo para visualizar el Dashboard
INSERT INTO inventory.categorias (id, sucursal_id, nombre, active, created_at)
VALUES 
    ('c1bc1aa0-4885-4fab-8bc2-19d75e10c5cc', '00000000-0000-0000-0000-000000000000', 'Alimentos', true, now()),
    ('c2bc1aa0-4885-4fab-8bc2-19d75e10c5cc', '00000000-0000-0000-0000-000000000000', 'Bebidas', true, now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventory.productos (id, sucursal_id, categoria_id, nombre, precio, precio_costo, stock_actual, stock_minimo, unidad, control_stock, active, created_at)
VALUES 
    (gen_random_uuid(), '00000000-0000-0000-0000-000000000000', 'c1bc1aa0-4885-4fab-8bc2-19d75e10c5cc', 'Hamburguesa Clásica', 18.50, 10.20, 45, 10, 'UND', true, true, now()),
    (gen_random_uuid(), '00000000-0000-0000-0000-000000000000', 'c1bc1aa0-4885-4fab-8bc2-19d75e10c5cc', 'Papas Fritas XL', 9.00, 3.50, 8, 15, 'UND', true, true, now()),
    (gen_random_uuid(), '00000000-0000-0000-0000-000000000000', 'c2bc1aa0-4885-4fab-8bc2-19d75e10c5cc', 'Coca Cola 500ml', 4.50, 2.10, 120, 24, 'UND', true, true, now()),
    (gen_random_uuid(), '00000000-0000-0000-0000-000000000000', 'c2bc1aa0-4885-4fab-8bc2-19d75e10c5cc', 'Inca Kola 500ml', 4.50, 2.10, 5, 24, 'UND', true, true, now())
ON CONFLICT (id) DO NOTHING;
