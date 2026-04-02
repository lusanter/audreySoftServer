package com.audrey.soft.auth.infrastructure.security.jwt;

import com.audrey.soft.auth.application.ports.out.TokenValidatorPort;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidatorAdapter implements TokenValidatorPort {

    private final JwtKeyProvider jwtKeyProvider;

    public JwtTokenValidatorAdapter(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    @Override
    public String extractSubject(String token) {
        // Lanza JwtException automáticamente si el token es inválido o expirado
        return Jwts.parser()
                .verifyWith(jwtKeyProvider.getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
