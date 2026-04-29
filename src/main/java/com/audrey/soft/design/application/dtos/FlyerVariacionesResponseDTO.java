package com.audrey.soft.design.application.dtos;

import java.util.List;

/**
 * Respuesta del Design Engine con múltiples variaciones del flyer.
 * Reemplaza AiFlyerDTO como respuesta principal del endpoint de generación.
 */
public record FlyerVariacionesResponseDTO(
        List<VariacionDTO> variaciones
) {}
