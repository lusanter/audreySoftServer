package com.audrey.soft.auth.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID id,
        boolean active,
        String username,
        String password,
        String email,
        String profilePictureUrl,
        LocalDateTime lastLogin,
        String document
) {
}
