package com.audrey.soft.auth.infrastructure.web.controllers;

import com.audrey.soft.auth.application.dtos.AuthResponseDTO;
import com.audrey.soft.auth.application.dtos.LoginRequestDTO;
import com.audrey.soft.auth.application.usecases.LoginUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }
}