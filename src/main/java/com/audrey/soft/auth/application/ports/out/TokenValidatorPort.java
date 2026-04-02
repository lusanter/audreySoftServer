package com.audrey.soft.auth.application.ports.out;

public interface TokenValidatorPort {
    /**
     * Valida la firma y expiración del token y retorna el subject (username).
     * Lanza excepción si el token es inválido o expirado.
     */
    String extractSubject(String token);
}
