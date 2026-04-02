package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.domain.models.RoleType;
import com.audrey.soft.auth.domain.models.ScopeType;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

import java.util.List;
import java.util.UUID;

public class AssignRoleUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleAssignmentRepositoryPort roleAssignmentRepository;

    public AssignRoleUseCase(UserRepositoryPort userRepository,
                             RoleAssignmentRepositoryPort roleAssignmentRepository) {
        this.userRepository = userRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    public UserRoleAssignment execute(UUID userId, RoleType roleType, ScopeType scopeType, UUID scopeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        // Validaciones de negocio: ¿Tiene sentido este rol en este scope?
        if (roleType == RoleType.SUPER_ADMIN && scopeType != ScopeType.PLATAFORMA) {
            throw new IllegalArgumentException("SUPER_ADMIN solo puede ser asignado a la PLATAFORMA.");
        }
        if (roleType == RoleType.ADMIN && scopeType != ScopeType.EMPRESA) {
            throw new IllegalArgumentException("ADMIN B2B solo puede ser asignado a nivel EMPRESA.");
        }
        if ((roleType == RoleType.CAJERO || roleType == RoleType.MOZO || roleType == RoleType.COCINERO)
                && scopeType != ScopeType.SUCURSAL) {
            throw new IllegalArgumentException("Roles operativos solo pueden ser asignados a nivel SUCURSAL.");
        }

        // Validar que no exista ya una asignación activa con el mismo rol/scope/scopeId
        List<UserRoleAssignment> existentes = roleAssignmentRepository.findActiveByUserId(userId);
        boolean duplicado = existentes.stream().anyMatch(a ->
                a.getRoleType() == roleType &&
                a.getScopeType() == scopeType &&
                (scopeId == null ? a.getScopeId() == null : scopeId.equals(a.getScopeId()))
        );

        if (duplicado) {
            throw new IllegalArgumentException("El usuario ya tiene este rol asignado en este contexto.");
        }

        // Crear la asignación
        UserRoleAssignment newAssignment = new UserRoleAssignment(
                null, user.getId(), roleType, scopeType, scopeId, true
        );

        return roleAssignmentRepository.save(newAssignment);
    }
}
