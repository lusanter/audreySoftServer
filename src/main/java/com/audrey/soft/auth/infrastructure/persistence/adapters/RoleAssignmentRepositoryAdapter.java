package com.audrey.soft.auth.infrastructure.persistence.adapters;

import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.infrastructure.persistence.entities.UserEntity;
import com.audrey.soft.auth.infrastructure.persistence.entities.UserRoleAssignmentEntity;
import com.audrey.soft.auth.infrastructure.persistence.repositories.SpringDataRoleAssignmentRepository;
import com.audrey.soft.auth.infrastructure.persistence.repositories.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoleAssignmentRepositoryAdapter implements RoleAssignmentRepositoryPort {

    private final SpringDataRoleAssignmentRepository assignmentJpa;
    private final SpringDataUserRepository userJpa;

    public RoleAssignmentRepositoryAdapter(SpringDataRoleAssignmentRepository assignmentJpa,
                                           SpringDataUserRepository userJpa) {
        this.assignmentJpa = assignmentJpa;
        this.userJpa = userJpa;
    }

    @Override
    public UserRoleAssignment save(UserRoleAssignment domain) {
        UserEntity userEntity = userJpa.findById(domain.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + domain.getUserId()));

        UserRoleAssignmentEntity entity = assignmentJpa.findById(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .orElseGet(() -> UserRoleAssignmentEntity.builder()
                        .user(userEntity)
                        .build());

        entity.setRoleType(domain.getRoleType());
        entity.setScopeType(domain.getScopeType());
        entity.setScopeId(domain.getScopeId());
        entity.setActive(domain.isActive());

        return toDomain(assignmentJpa.save(entity));
    }

    @Override
    public List<UserRoleAssignment> findActiveByUserId(UUID userId) {
        return assignmentJpa.findActiveByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<UserRoleAssignment> findById(UUID id) {
        return assignmentJpa.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        assignmentJpa.deleteById(id);
    }

    private UserRoleAssignment toDomain(UserRoleAssignmentEntity entity) {
        return new UserRoleAssignment(
                entity.getId(),
                entity.getUser().getId(),
                entity.getRoleType(),
                entity.getScopeType(),
                entity.getScopeId(),
                entity.isActive()
        );
    }
}
