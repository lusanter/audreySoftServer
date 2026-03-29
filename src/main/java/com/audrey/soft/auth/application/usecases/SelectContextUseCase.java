package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.AuthResponseDTO;
import com.audrey.soft.auth.application.dtos.ContextSelectionRequestDTO;
import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

public class SelectContextUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleAssignmentRepositoryPort roleAssignmentRepository;
    private final TokenGeneratorPort tokenGenerator;

    public SelectContextUseCase(UserRepositoryPort userRepository,
                                RoleAssignmentRepositoryPort roleAssignmentRepository,
                                TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.tokenGenerator = tokenGenerator;
    }

    public AuthResponseDTO execute(String usernameClaim, ContextSelectionRequestDTO request) {
        User user = userRepository.findByUsername(usernameClaim)
                .orElseThrow(() -> new RuntimeException("Usuario extraído del token no existe."));

        UserRoleAssignment chosenAssignment = roleAssignmentRepository.findById(request.assignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Asignación de rol no encontrada."));

        // Validar seguridad crítica: Asegurar que el UUID de asignación 
        // enviado SÍ pertenece al usuario autenticado. (Para evitar robos horizontales de sesión).
        if (!chosenAssignment.getUserId().equals(user.getId()) || !chosenAssignment.isActive()) {
            throw new SecurityException("No tienes permiso para acceder a este contexto de trabajo.");
        }

        // Generamos el AccessToken FINAL definitivo y un Refresh Token operativo
        return new AuthResponseDTO(
                tokenGenerator.generateFinalToken(user, chosenAssignment),
                tokenGenerator.generateRefreshToken(user),
                false, // Ya se completó la transición
                user.getId(),
                null,
                chosenAssignment.getRoleType().name(),
                chosenAssignment.getScopeType().name(),
                chosenAssignment.getScopeId()
        );
    }
}
