package com.audrey.soft.auth.infrastructure.security;

import com.audrey.soft.auth.domain.models.RoleType;
import com.audrey.soft.auth.domain.models.ScopeType;

import java.util.UUID;

/**
 * Principal inyectado por Spring Security.
 * Contiene el contexto completo extraído del JWT Final.
 * Si isIntermediate es true, significa que el JWT no tiene el resto de campos.
 */
public record AudreyAuthPrincipal(
        String username,
        boolean isIntermediate,
        RoleType role,
        ScopeType scopeType,
        UUID scopeId
) {
    public AudreyAuthPrincipal(String username) {
        // Constructor para un token intermedio o básico
        this(username, true, null, null, null);
    }
}
