package com.audrey.soft.auth.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        boolean active,
        String username,
        String password,
        String email,
        String role,
        UUID restauranteId,
        LocalDateTime lastLogin
) {}
