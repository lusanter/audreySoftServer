package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.AuthResponseDTO;
import com.audrey.soft.auth.application.dtos.LoginRequestDTO;
import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.domain.exceptions.InvalidCredentialsException;
import com.audrey.soft.auth.domain.exceptions.UserNotActiveException;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public LoginUseCase(UserRepositoryPort userRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    public AuthResponseDTO execute(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isActive()) {
            throw new UserNotActiveException(user.getUsername());
        }

        user.updateLastLogin();
        userRepository.save(user);

        return new AuthResponseDTO(
                tokenGenerator.generateAccessToken(user),
                tokenGenerator.generateRefreshToken(user),
                user.getId(),
                user.getRole().name(),
                user.getRestauranteId()
        );
    }
}