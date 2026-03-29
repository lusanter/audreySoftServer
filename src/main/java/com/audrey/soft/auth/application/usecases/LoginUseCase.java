package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.AuthResponseDTO;
import com.audrey.soft.auth.application.dtos.UserContextDTO;
import com.audrey.soft.auth.application.dtos.LoginRequestDTO;
import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.domain.exceptions.InvalidCredentialsException;
import com.audrey.soft.auth.domain.exceptions.UserNotActiveException;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;
import com.audrey.soft.tenant.domain.models.Empresa;
import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleAssignmentRepositoryPort roleAssignmentRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    
    // Para resolver los nombres amigables de empresa o sucursal
    private final EmpresaRepositoryPort empresaRepository;
    private final SucursalRepositoryPort sucursalRepository;

    public LoginUseCase(UserRepositoryPort userRepository,
                        RoleAssignmentRepositoryPort roleAssignmentRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenGeneratorPort tokenGenerator,
                        EmpresaRepositoryPort empresaRepository,
                        SucursalRepositoryPort sucursalRepository) {
        this.userRepository = userRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.empresaRepository = empresaRepository;
        this.sucursalRepository = sucursalRepository;
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

        // Buscar qué permisos tiene asignados
        List<UserRoleAssignment> assignments = roleAssignmentRepository.findActiveByUserId(user.getId());

        if (assignments.isEmpty()) {
            throw new IllegalArgumentException("El usuario no tiene ningún perfil asignado para operar.");
        }

        // --- SMART LOGIN ---
        if (assignments.size() == 1) {
            // Un único contexto disponible: Emite JWT Final directo + Datos Reales
            UserRoleAssignment singleContext = assignments.get(0);
            
            return new AuthResponseDTO(
                    tokenGenerator.generateFinalToken(user, singleContext),
                    tokenGenerator.generateRefreshToken(user),
                    false, // No requiere elegir contexto
                    user.getId(),
                    null,
                    singleContext.getRoleType().name(),
                    singleContext.getScopeType().name(),
                    singleContext.getScopeId()
            );
        }

        // --- MÚLTIPLES CONTEXTOS ---
        // Emitir JWT Intermedio y listar opciones
        List<UserContextDTO> contextsList = new ArrayList<>();
        
        for (UserRoleAssignment assignment : assignments) {
            String contextName = resolveContextName(assignment);
            contextsList.add(new UserContextDTO(
                    assignment.getId(),
                    assignment.getScopeType(),
                    assignment.getScopeId(),
                    assignment.getRoleType(),
                    contextName
            ));
        }

        return new AuthResponseDTO(
                tokenGenerator.generateIntermediateToken(user),
                null, // Aún no damos RefreshToken definitivo hasta que elijan contexto
                true, // Requiere que invoquen a /context
                user.getId(),
                contextsList,
                null, null, null
        );
    }

    private String resolveContextName(UserRoleAssignment assignment) {
        switch (assignment.getScopeType()) {
            case PLATAFORMA: return "Sistema Audrey Central";
            case EMPRESA:
                return empresaRepository.findById(assignment.getScopeId())
                        .map(Empresa::getNombre)
                        .orElse("Empresa Desconocida o Inactiva");
            case SUCURSAL:
                return sucursalRepository.findById(assignment.getScopeId())
                        .map(Sucursal::getNombre)
                        .orElse("Sucursal Desconocida o Inactiva");
            default: return "Contexto Abstracto";
        }
    }
}