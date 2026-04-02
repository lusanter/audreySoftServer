package com.audrey.soft.auth.infrastructure.web;

import com.audrey.soft.auth.application.dtos.AssignRoleRequestDTO;
import com.audrey.soft.auth.application.dtos.RoleAssignmentDTO;
import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.application.usecases.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/founders")
public class FounderController {

    private final CreateUserByFounderUseCase createUserUseCase;
    private final UpdateUserByFounderUseCase updateUserUseCase;
    private final DeactivateUserByFounderUseCase deactivateUserUseCase;
    private final ListUserUseCase listUserUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final ListUserAssignmentsUseCase listUserAssignmentsUseCase;
    private final RevokeRoleUseCase revokeRoleUseCase;

    public FounderController(CreateUserByFounderUseCase createUserUseCase,
                             UpdateUserByFounderUseCase updateUserUseCase,
                             DeactivateUserByFounderUseCase desactivateUserUseCase,
                             ListUserUseCase listUserUseCase,
                             AssignRoleUseCase assignRoleUseCase,
                             ListUserAssignmentsUseCase listUserAssignmentsUseCase,
                             RevokeRoleUseCase revokeRoleUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deactivateUserUseCase = desactivateUserUseCase;
        this.listUserUseCase = listUserUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.listUserAssignmentsUseCase = listUserAssignmentsUseCase;
        this.revokeRoleUseCase = revokeRoleUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserUseCase.execute(request));
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UserDTO request) {
        updateUserUseCase.execute(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        deactivateUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list/users")
    public ResponseEntity<List<UserDTO>> listarUsuarios() {
        return ResponseEntity.ok(listUserUseCase.execute());
    }

    /** Asignar un rol/scope a un usuario */
    @PostMapping("/{userId}/roles")
    public ResponseEntity<RoleAssignmentDTO> assignRole(
            @PathVariable UUID userId,
            @RequestBody AssignRoleRequestDTO request) {
        var assignment = assignRoleUseCase.execute(userId, request.roleType(), request.scopeType(), request.scopeId());
        var dto = new RoleAssignmentDTO(assignment.getId(), assignment.getUserId(),
                assignment.getRoleType(), assignment.getScopeType(), assignment.getScopeId(), assignment.isActive());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Listar todos los roles activos de un usuario */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleAssignmentDTO>> listRoles(@PathVariable UUID userId) {
        return ResponseEntity.ok(listUserAssignmentsUseCase.execute(userId));
    }

    /** Revocar una asignación de rol */
    @DeleteMapping("/roles/{assignmentId}")
    public ResponseEntity<Void> revokeRole(@PathVariable UUID assignmentId) {
        revokeRoleUseCase.execute(assignmentId);
        return ResponseEntity.noContent().build();
    }
}
