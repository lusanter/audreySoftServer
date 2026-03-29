package com.audrey.soft.auth.infrastructure.security.jwt;

import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenAdapter implements TokenGeneratorPort {

    private final JwtKeyProvider jwtKeyProvider;

    @Value("${audrey.security.jwt.access-token-expiration}")
    private long jwtExpiration;

    @Value("${audrey.security.jwt.refresh-token-expiration}")
    private long refreshExpiration;

    public JwtTokenAdapter(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    /** 
     * JWT Intermedio: El usuario tiene credenciales válidas pero tiene
     * que elegir un contexto de trabajo usando /auth/context.
     */
    @Override
    public String generateIntermediateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("intermediate", true); // Flag vital
        return buildToken(claims, user, jwtExpiration);
    }

    /** 
     * JWT Final: El usuario ya tiene un contexto definido, los Claims
     * viajan embebidos para dar acceso al sistema de verdad.
     */
    @Override
    public String generateFinalToken(User user, UserRoleAssignment assignment) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("intermediate", false);
        claims.put("role", assignment.getRoleType().name());
        claims.put("scope_type", assignment.getScopeType().name());
        
        if (assignment.getScopeId() != null) {
            claims.put("scope_id", assignment.getScopeId().toString());
        }
        
        return buildToken(claims, user, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtKeyProvider.getSignInKey())
                .compact();
    }
}