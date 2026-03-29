package com.audrey.soft.auth.infrastructure.security.filters;

import com.audrey.soft.auth.domain.models.RoleType;
import com.audrey.soft.auth.domain.models.ScopeType;
import com.audrey.soft.auth.infrastructure.security.AudreyAuthPrincipal;
import com.audrey.soft.auth.infrastructure.security.jwt.JwtKeyProvider;
import com.audrey.soft.shared.TenantContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtKeyProvider jwtKeyProvider;

    public JwtAuthenticationFilter(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            Claims claims = Jwts.parser()
                    .verifyWith(jwtKeyProvider.getSignInKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            String username = claims.getSubject();
            Boolean isIntermediate = claims.get("intermediate", Boolean.class);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                AudreyAuthPrincipal principal;
                SimpleGrantedAuthority authority;

                if (Boolean.TRUE.equals(isIntermediate)) {
                    // Token visitante/intermedio. Tiene 1 rol de fantasía que solo 
                    // le deja acceder a /api/v1/auth/context
                    principal = new AudreyAuthPrincipal(username);
                    authority = new SimpleGrantedAuthority("ROLE_INTERMEDIATE");
                } else {
                    // Token Final de un empleado con contexto validado
                    String roleStr = claims.get("role", String.class);
                    String scopeTypeStr = claims.get("scope_type", String.class);
                    String scopeIdStr = claims.get("scope_id", String.class);
                    
                    RoleType role = RoleType.valueOf(roleStr);
                    ScopeType scopeType = ScopeType.valueOf(scopeTypeStr);
                    UUID scopeId = scopeIdStr != null ? UUID.fromString(scopeIdStr) : null;

                    principal = new AudreyAuthPrincipal(username, false, role, scopeType, scopeId);
                    authority = new SimpleGrantedAuthority("ROLE_" + role.name());

                    // SETEAR EL CONTEXTO CORPORATIVO/SUCURSAL DE FORMA MÁGICA Y GLOBAL
                    if (scopeType == ScopeType.EMPRESA && scopeId != null) {
                        TenantContext.setEmpresaId(scopeId);
                    } else if (scopeType == ScopeType.SUCURSAL && scopeId != null) {
                        TenantContext.setSucursalId(scopeId);
                    }
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.singletonList(authority));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            TenantContext.clear();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Extremadamente importante para no fugar datos entre requests de distintos threads
            TenantContext.clear();
        }
    }
}