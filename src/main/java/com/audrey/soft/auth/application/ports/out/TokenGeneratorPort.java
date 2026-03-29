package com.audrey.soft.auth.application.ports.out;

import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.models.UserRoleAssignment;

public interface TokenGeneratorPort {

    /** 
     * JWT Temporal: Devuelve un token que solo sirve para la ruta /auth/context, 
     * no contiene permisos (roles) mapeados a claims. 
     */
    String generateIntermediateToken(User user);

    /**
     * JWT Final: Este token sí contiene un Rol validado, y los IDs del 
     * Scope (Empresa o Sucursal) para operar con normalidad.
     */
    String generateFinalToken(User user, UserRoleAssignment assignment);

    String generateRefreshToken(User user);
}
