package com.audrey.soft.config;

import com.audrey.soft.auth.domain.models.RoleType;
import com.audrey.soft.auth.domain.models.ScopeType;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import com.audrey.soft.auth.domain.ports.PasswordEncoderPort;
import com.audrey.soft.auth.domain.ports.RoleAssignmentRepositoryPort;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepository;
    private final RoleAssignmentRepositoryPort roleAssignmentRepository;
    private final PasswordEncoderPort passwordEncoder;

    public DatabaseSeeder(UserRepositoryPort userRepository,
                          RoleAssignmentRepositoryPort roleAssignmentRepository,
                          PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String founderUsername = "santer";
        
        if (userRepository.findByUsername(founderUsername).isEmpty()) {
            System.out.println("🌱 [SEEDER] Inicializando Base de Datos... Creando al Dios Fundador.");

            // 1. Creamos la identidad del usuario
            User founder = new User(
                    null,
                    founderUsername,
                    passwordEncoder.encode("1234"),
                    "santer@audrey.com",
                    null,
                    true,
                    null,
                    null,
                    null
            );
            founder = userRepository.save(founder);

            // 2. Le asignamos los poderes ilimitados en el libro de permisos
            UserRoleAssignment godMode = new UserRoleAssignment(
                    null,
                    founder.getId(),
                    RoleType.SUPER_ADMIN,
                    ScopeType.PLATAFORMA,
                    null,
                    true
            );
            roleAssignmentRepository.save(godMode);
            
        } else {
            System.out.println("🌱 [SEEDER] El fundador ya existe. No es necesario poblar.");
        }
    }
}
