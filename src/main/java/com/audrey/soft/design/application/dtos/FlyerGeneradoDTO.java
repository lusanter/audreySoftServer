package com.audrey.soft.design.application.dtos;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para el historial de flyers generados.
 */
public record FlyerGeneradoDTO(
        UUID id,
        List<String> productoIds,
        String tipoUso,
        String formato,
        FlyerRequestDTO.PaletteDTO palette,
        FlyerVariacionesResponseDTO aiResult,
        UUID plantillaId,
        Instant createdAt,
        Instant updatedAt
) {}
