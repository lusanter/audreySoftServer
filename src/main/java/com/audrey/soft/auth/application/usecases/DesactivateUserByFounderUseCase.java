package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.domain.exceptions.UserNotFoundException;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

import java.util.UUID;

public class DesactivateUserByFounderUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DesactivateUserByFounderUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public void execute(UUID userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.desactivate();
        userRepositoryPort.save(user);
    }
}
