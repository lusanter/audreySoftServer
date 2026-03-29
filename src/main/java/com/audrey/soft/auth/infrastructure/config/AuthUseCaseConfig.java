package com.audrey.soft.auth.infrastructure.config;

import com.audrey.soft.auth.application.mappers.UserMapper;
import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.application.usecases.*;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public LoginUseCase loginUseCase(
            UserRepositoryPort userRepository,
            RoleAssignmentRepositoryPort roleAssignmentRepository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator,
            EmpresaRepositoryPort empresaRepository,
            SucursalRepositoryPort sucursalRepository) {
        return new LoginUseCase(userRepository, roleAssignmentRepository, passwordEncoder, tokenGenerator, empresaRepository, sucursalRepository);
    }

    @Bean
    public SelectContextUseCase selectContextUseCase(
            UserRepositoryPort userRepository,
            RoleAssignmentRepositoryPort roleAssignmentRepository,
            TokenGeneratorPort tokenGenerator) {
        return new SelectContextUseCase(userRepository, roleAssignmentRepository, tokenGenerator);
    }

    @Bean
    public AssignRoleUseCase assignRoleUseCase(
            UserRepositoryPort userRepository,
            RoleAssignmentRepositoryPort roleAssignmentRepository) {
        return new AssignRoleUseCase(userRepository, roleAssignmentRepository);
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
    public DeactivateUserByFounderUseCase deactivateUserByFounderUseCase(
            UserRepositoryPort userRepository) {
        return new DeactivateUserByFounderUseCase(userRepository);
    }
}