package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.application.mappers.UserMapper;
import com.audrey.soft.auth.domain.exceptions.UsernameAlreadyExistsException;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

public class CreateUserByFounderUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserMapper userMapper;

    public CreateUserByFounderUseCase(UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDTO execute(UserDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        // El usuario nace "vacío" de permisos. Luego se llamará a AssignRoleUseCase.
        User newUser = new User(
                null,
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                null,
                true,
                null,
                null,
                null);

        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }
}