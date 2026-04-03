-- liquibase formatted sql

-- changeset santer:04-crear-tablas-usuarios
CREATE TABLE IF NOT EXISTS core.usuarios (
    id uuid PRIMARY KEY,
    document varchar(20) UNIQUE NOT NULL,
    username varchar(50) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    email varchar(100) UNIQUE NOT NULL,
    profile_picture_url varchar(255),
    active boolean NOT NULL DEFAULT true,
    last_login timestamp,
    created_at timestamp,
    updated_at timestamp
);
-- rollback DROP TABLE IF EXISTS core.usuarios;

-- changeset santer:04-crear-tablas-user-role-assignments
CREATE TABLE IF NOT EXISTS core.user_role_assignments (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    role_type varchar(30) NOT NULL,
    scope_type varchar(30) NOT NULL,
    scope_id uuid,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL
);
-- rollback DROP TABLE IF EXISTS core.user_role_assignments;

-- changeset santer:04-crear-tablas-auth-add-fks-and-uniques
ALTER TABLE core.user_role_assignments ADD CONSTRAINT fk_user_role_assignments_user FOREIGN KEY (user_id) REFERENCES core.usuarios (id) ON DELETE CASCADE;
ALTER TABLE core.user_role_assignments ADD CONSTRAINT uq_user_role_scope UNIQUE (user_id, role_type, scope_type, scope_id);
-- rollback ALTER TABLE core.user_role_assignments DROP CONSTRAINT IF EXISTS fk_user_role_assignments_user; ALTER TABLE core.user_role_assignments DROP CONSTRAINT IF EXISTS uq_user_role_scope;

