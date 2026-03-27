package com.audrey.soft.auth.application.ports.out;

import com.audrey.soft.auth.domain.models.User;

public interface TokenGeneratorPort {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
}
