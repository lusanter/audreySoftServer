package com.audrey.soft.auth.infrastructure.web;

import com.audrey.soft.auth.application.dtos.AuthResponseDTO;
import com.audrey.soft.auth.application.dtos.ContextSelectionRequestDTO;
import com.audrey.soft.auth.application.dtos.LoginRequestDTO;
import com.audrey.soft.auth.application.usecases.LoginUseCase;
import com.audrey.soft.auth.application.usecases.SelectContextUseCase;
import com.audrey.soft.auth.infrastructure.security.AudreyAuthPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final SelectContextUseCase selectContextUseCase;

    public AuthController(LoginUseCase loginUseCase, SelectContextUseCase selectContextUseCase) {
        this.loginUseCase = loginUseCase;
        this.selectContextUseCase = selectContextUseCase;
    }

    /**
     * Paso 1: Login. Puede devolver el token final directamente, o un token intermedio
     * con una lista de contextos si el usuario es multi-rol.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }

    /**
     * Paso 2: Selección de contexto. Requerida solo si el paso 1 devolvió 'requireContextSelection: true'.
     * Requiere el token Intermedio en el header Authorization.
     */
    @PostMapping("/context")
    @PreAuthorize("hasRole('INTERMEDIATE')") 
    // ^ Esta regla protege el endpoint obligando a que traigan el token válido intermedio
    public ResponseEntity<AuthResponseDTO> selectContext(
            @AuthenticationPrincipal AudreyAuthPrincipal principal,
            @RequestBody ContextSelectionRequestDTO request) {
        
        return ResponseEntity.ok(selectContextUseCase.execute(principal.username(), request));
    }
}