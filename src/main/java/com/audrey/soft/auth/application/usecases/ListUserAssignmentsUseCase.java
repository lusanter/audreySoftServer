package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.RoleAssignmentDTO;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListUserAssignmentsUseCase {

    private final RoleAssignmentRepositoryPort roleAssignmentRepository;

    public ListUserAssignmentsUseCase(RoleAssignmentRepositoryPort roleAssignmentRepository) {
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    public List<RoleAssignmentDTO> execute(UUID userId) {
        return roleAssignmentRepository.findActiveByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    private RoleAssignmentDTO toDto(UserRoleAssignment a) {
        return new RoleAssignmentDTO(a.getId(), a.getUserId(), a.getRoleType(), a.getScopeType(), a.getScopeId(), a.isActive());
    }
}
