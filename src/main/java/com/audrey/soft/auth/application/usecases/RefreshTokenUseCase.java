package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.RefreshTokenRequestDTO;
import com.audrey.soft.auth.application.dtos.RefreshTokenResponseDTO;
import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.application.ports.out.TokenValidatorPort;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

import java.util.List;

public class RefreshTokenUseCase {

    private final TokenValidatorPort tokenValidator;
    private final TokenGeneratorPort tokenGenerator;
    private final UserRepositoryPort userRepository;
    private final RoleAssignmentRepositoryPort roleAssignmentRepository;

    public RefreshTokenUseCase(TokenValidatorPort tokenValidator,
                               TokenGeneratorPort tokenGenerator,
                               UserRepositoryPort userRepository,
                               RoleAssignmentRepositoryPort roleAssignmentRepository) {
        this.tokenValidator = tokenValidator;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    public RefreshTokenResponseDTO execute(RefreshTokenRequestDTO request) {
        // Validar y extraer el subject del refresh token
        String username = tokenValidator.extractSubject(request.refreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Usuario del refresh token no encontrado."));

        if (!user.isActive()) {
            throw new SecurityException("El usuario está desactivado.");
        }

        // Determinar qué tipo de access token regenerar
        // Si el usuario tiene un único contexto, emitimos el token final directamente.
        // Si tiene múltiples, emitimos un token intermedio (deberá volver a elegir contexto).
        List<UserRoleAssignment> assignments = roleAssignmentRepository.findActiveByUserId(user.getId());

        if (assignments.isEmpty()) {
            throw new SecurityException("El usuario no tiene perfiles activos.");
        }

        String newAccessToken;
        if (assignments.size() == 1) {
            newAccessToken = tokenGenerator.generateFinalToken(user, assignments.get(0));
        } else {
            newAccessToken = tokenGenerator.generateIntermediateToken(user);
        }

        String newRefreshToken = tokenGenerator.generateRefreshToken(user);

        return new RefreshTokenResponseDTO(newAccessToken, newRefreshToken);
    }
}
