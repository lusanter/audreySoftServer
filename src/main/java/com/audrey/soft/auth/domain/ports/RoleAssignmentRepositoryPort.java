package com.audrey.soft.auth.domain.ports;

import com.audrey.soft.auth.domain.models.UserRoleAssignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleAssignmentRepositoryPort {
    UserRoleAssignment save(UserRoleAssignment assignment);
    List<UserRoleAssignment> findActiveByUserId(UUID userId);
    Optional<UserRoleAssignment> findById(UUID id);
    void deleteById(UUID id);
}
