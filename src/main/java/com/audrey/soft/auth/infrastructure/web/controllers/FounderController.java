package com.audrey.soft.auth.infrastructure.web.controllers;

import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.application.usecases.CreateUserByFounderUseCase;
import com.audrey.soft.auth.application.usecases.DesactivateUserByFounderUseCase;
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
    private final DesactivateUserByFounderUseCase desactivateUserUseCase;

    public FounderController(CreateUserByFounderUseCase createUserUseCase,
                             UpdateUserByFounderUseCase updateUserUseCase,
                             DesactivateUserByFounderUseCase desactivateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.desactivateUserUseCase = desactivateUserUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UserDTO request) {
        updateUserUseCase.execute(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        desactivateUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
