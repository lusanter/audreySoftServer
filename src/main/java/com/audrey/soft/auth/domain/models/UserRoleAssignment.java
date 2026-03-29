package com.audrey.soft.auth.domain.models;

import java.util.UUID;

/**
 * Tabla universal de asignación de roles de Audrey (Scoped RBAC).
 * Un User no "es" un cajero. Un user "Tiene asignado" el RolType CAJERO 
 * dentro del ScopeType SUCURSAL apuntando al ScopeId de la sucursal Norte.
 */
public class UserRoleAssignment {

    private UUID id;
    private UUID userId;
    private RoleType roleType;
    private ScopeType scopeType;
    private UUID scopeId;
    private boolean active;

    public UserRoleAssignment(UUID id, UUID userId, RoleType roleType,
                              ScopeType scopeType, UUID scopeId, boolean active) {
        this.id = id;
        this.userId = userId;
        this.roleType = roleType;
        this.scopeType = scopeType;
        this.scopeId = scopeId;
        this.active = active;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }

    public ScopeType getScopeType() { return scopeType; }
    public void setScopeType(ScopeType scopeType) { this.scopeType = scopeType; }

    public UUID getScopeId() { return scopeId; }
    public void setScopeId(UUID scopeId) { this.scopeId = scopeId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
