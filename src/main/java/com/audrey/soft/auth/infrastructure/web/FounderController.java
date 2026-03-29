package com.audrey.soft.auth.infrastructure.web;

import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.application.usecases.CreateUserByFounderUseCase;
import com.audrey.soft.auth.application.usecases.DeactivateUserByFounderUseCase;
import com.audrey.soft.auth.application.usecases.UpdateUserByFounderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/founders")
public class FounderController {

    private final CreateUserByFounderUseCase createUserUseCase;
    private final UpdateUserByFounderUseCase updateUserUseCase;
    private final DeactivateUserByFounderUseCase deactivateUserUseCase;

    public FounderController(CreateUserByFounderUseCase createUserUseCase,
                             UpdateUserByFounderUseCase updateUserUseCase,
                             DeactivateUserByFounderUseCase desactivateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deactivateUserUseCase = desactivateUserUseCase;
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
}
