package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;

import java.util.UUID;

public class RevokeRoleUseCase {

    private final RoleAssignmentRepositoryPort roleAssignmentRepository;

    public RevokeRoleUseCase(RoleAssignmentRepositoryPort roleAssignmentRepository) {
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    public void execute(UUID assignmentId) {
        roleAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada: " + assignmentId));
        roleAssignmentRepository.deleteById(assignmentId);
    }
}
