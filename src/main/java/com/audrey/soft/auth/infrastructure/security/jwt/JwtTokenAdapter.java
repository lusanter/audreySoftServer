package com.audrey.soft.auth.infrastructure.security.jwt;

import com.audrey.soft.auth.application.ports.out.TokenGeneratorPort;
import com.audrey.soft.auth.domain.models.User;
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

    @Override
    public String generateAccessToken(User user) {
        return buildToken(buildExtraClaims(user), user, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private Map<String, Object> buildExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("restaurante_id",
                user.getRestauranteId() != null ? user.getRestauranteId().toString() : null);
        return extraClaims;
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