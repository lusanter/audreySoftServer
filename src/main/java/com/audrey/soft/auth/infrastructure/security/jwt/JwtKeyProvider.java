package com.audrey.soft.auth.infrastructure.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Componente único responsable de proporcionar la clave HMAC para firmar/verificar JWT.
 * Evita la duplicación del método getSignInKey() en JwtTokenAdapter y JwtAuthenticationFilter.
 */
@Component
public class JwtKeyProvider {

    @Value("${audrey.security.jwt.secret-key}")
    private String secretKey;

    public SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
