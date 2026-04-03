-- liquibase formatted sql

-- changeset santer:06-seed-admin-user runOnChange:false
-- comment: Crea el usuario admin (SUPER_ADMIN) inicial. Se ejecuta en todos los entornos.
INSERT INTO core.usuarios (id, document, username, password, email, active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '00000000',
    'admin',
    '$2a$10$.iMVbLD11kB4DySNRtIiyu2365gPhUByvcdR0uM2t5EUveucTqIQ6',
    'admin@audrey.com',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;
-- rollback DELETE FROM core.usuarios WHERE username = 'admin';

-- changeset santer:06-seed-admin-role runOnChange:false
-- comment: Asigna el rol SUPER_ADMIN global al usuario admin.
INSERT INTO core.user_role_assignments (id, user_id, role_type, scope_type, scope_id, active, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    'SUPER_ADMIN',
    'PLATAFORMA',
    NULL,
    true,
    NOW()
FROM core.usuarios u
WHERE u.username = 'admin'
ON CONFLICT DO NOTHING;
-- rollback DELETE FROM core.user_role_assignments ura USING core.usuarios u WHERE ura.user_id = u.id AND u.username = 'admin';
