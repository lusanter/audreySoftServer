-- liquibase formatted sql

-- changeset santer:05-idx-sucursales-empresa-id
-- comment: Índice en FK empresa_id para evitar full scan en JOINs empresa->sucursales
CREATE INDEX IF NOT EXISTS idx_sucursales_empresa_id ON core.sucursales(empresa_id);
-- rollback DROP INDEX IF EXISTS idx_sucursales_empresa_id;

-- changeset santer:05-idx-user-role-assignments-user-id
-- comment: Índice en FK user_id para lookups rápidos de roles por usuario
CREATE INDEX IF NOT EXISTS idx_user_role_assignments_user_id ON core.user_role_assignments(user_id);
-- rollback DROP INDEX IF EXISTS idx_user_role_assignments_user_id;

-- changeset santer:05-idx-user-role-assignments-scope-id
-- comment: Índice parcial en scope_id para filtros por empresa o sucursal (excluye NULLs de scope PLATAFORMA)
CREATE INDEX IF NOT EXISTS idx_user_role_assignments_scope_id ON core.user_role_assignments(scope_id) WHERE scope_id IS NOT NULL;
-- rollback DROP INDEX IF EXISTS idx_user_role_assignments_scope_id;

-- changeset santer:05-idx-empresas-nombre
-- comment: Índice en nombre para búsquedas y autocomplete de empresas
CREATE INDEX IF NOT EXISTS idx_empresas_nombre ON core.empresas(nombre);
-- rollback DROP INDEX IF EXISTS idx_empresas_nombre;
