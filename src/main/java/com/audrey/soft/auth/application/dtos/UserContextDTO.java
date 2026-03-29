package com.audrey.soft.auth.application.dtos;

import com.audrey.soft.auth.domain.models.RoleType;
import com.audrey.soft.auth.domain.models.ScopeType;

import java.util.UUID;

/**
 * Representa un "Perfil" o "Contexto" al que el usuario tiene acceso.
 * Si el usuario es dueño de múltiples empresas o sucursales, 
 * recibirá una lista de estos DTOs tras el primer paso del login.
 */
public record UserContextDTO(
        UUID assignmentId,
        ScopeType scopeType,
        UUID scopeId,
        RoleType role,
        String contextName // Ej: "Plataforma Audrey", "KFC", "KFC - Centro"
) {}
