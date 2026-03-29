package com.audrey.soft.auth.application.dtos;

import java.util.List;
import java.util.UUID;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        boolean requireContextSelection,
        UUID userId,
        
        // Propiedades rellenadas si requireContextSelection = true
        List<UserContextDTO> availableContexts,
        
        // Propiedades rellenadas si requireContextSelection = false
        // (es decir, el token final ya fue emitido y estos son sus datos)
        String activeRole,
        String activeScopeType,
        UUID activeScopeId
) {}
