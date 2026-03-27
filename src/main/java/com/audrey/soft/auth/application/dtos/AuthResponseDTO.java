package com.audrey.soft.auth.application.dtos;

import java.util.UUID;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        UUID userId,
        String role,
        UUID restauranteId
) {}
