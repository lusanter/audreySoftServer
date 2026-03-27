package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.domain.exceptions.UserNotFoundException;
import com.audrey.soft.auth.domain.models.Role;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

import java.util.UUID;

public class UpdateUserByFounderUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoder;

    public UpdateUserByFounderUseCase(UserRepositoryPort userRepositoryPort,
                                      PasswordEncoderPort passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(UUID userId, UserDTO userDTO) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setRole(Role.valueOf(userDTO.role().toUpperCase()));
        user.setRestauranteId(userDTO.restauranteId());

        if (userDTO.active()) {
            user.activate();
        } else {
            user.desactivate();
        }

        // Solo re-hashear si viene un nuevo password (no vacío)
        if (userDTO.password() != null && !userDTO.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.password()));
        }

        userRepositoryPort.save(user);
    }
}
