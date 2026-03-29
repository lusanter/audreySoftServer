package com.audrey.soft.auth.infrastructure.persistence.repositories;

import com.audrey.soft.auth.infrastructure.persistence.entities.UserRoleAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataRoleAssignmentRepository extends JpaRepository<UserRoleAssignmentEntity, UUID> {

    @Query("SELECT r FROM UserRoleAssignmentEntity r WHERE r.user.id = :userId AND r.active = true")
    List<UserRoleAssignmentEntity> findActiveByUserId(@Param("userId") UUID userId);

}
