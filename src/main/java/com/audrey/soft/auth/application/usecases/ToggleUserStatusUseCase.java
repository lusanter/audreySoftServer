package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.domain.exceptions.UserNotFoundException;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

import java.util.UUID;

public class ToggleUserStatusUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ToggleUserStatusUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public void execute(UUID userId, boolean activate) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (activate) user.activate();
        else user.desactivate();
        userRepositoryPort.save(user);
    }
}
