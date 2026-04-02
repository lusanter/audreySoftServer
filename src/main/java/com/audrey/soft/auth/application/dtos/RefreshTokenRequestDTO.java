package com.audrey.soft.auth.application.dtos;

public record RefreshTokenRequestDTO(String refreshToken) {
    public RefreshTokenRequestDTO {
        if (refreshToken == null || refreshToken.isBlank())
            throw new IllegalArgumentException("El refreshToken no puede estar vacío.");
    }
}
