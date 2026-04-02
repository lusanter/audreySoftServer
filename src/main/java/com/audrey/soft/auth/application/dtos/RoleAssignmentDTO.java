package com.audrey.soft.auth.application.dtos;

import com.audrey.soft.auth.domain.models.RoleType;
import com.audrey.soft.auth.domain.models.ScopeType;

import java.util.UUID;

public record RoleAssignmentDTO(
        UUID id,
        UUID userId,
        RoleType roleType,
        ScopeType scopeType,
        UUID scopeId,
        boolean active
) {}
