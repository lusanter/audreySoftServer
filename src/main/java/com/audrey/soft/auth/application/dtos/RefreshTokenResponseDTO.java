package com.audrey.soft.auth.application.dtos;

public record RefreshTokenResponseDTO(
        String accessToken,
        String refreshToken
) {}
