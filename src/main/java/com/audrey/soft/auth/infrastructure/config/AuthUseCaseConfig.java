package com.audrey.soft.auth.infrastructure.config;

import com.audrey.soft.auth.application.mappers.UserMapper;
import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.application.usecases.CreateUserByFounderUseCase;
import com.audrey.soft.auth.application.usecases.DesactivateUserByFounderUseCase;
import com.audrey.soft.auth.application.usecases.LoginUseCase;
import com.audrey.soft.auth.application.usecases.UpdateUserByFounderUseCase;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public LoginUseCase loginUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenGenerator);
    }

    @Bean
    public CreateUserByFounderUseCase createUserByFounderUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            UserMapper userMapper) {
        return new CreateUserByFounderUseCase(userRepository, passwordEncoder, userMapper);
    }

    @Bean
    public UpdateUserByFounderUseCase updateUserByFounderUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder) {
        return new UpdateUserByFounderUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public DesactivateUserByFounderUseCase desactivateUserByFounderUseCase(
            UserRepositoryPort userRepository) {
        return new DesactivateUserByFounderUseCase(userRepository);
    }
}