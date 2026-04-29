package com.audrey.soft.design.application.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record PlantillaResponseDTO(
        String id,
        String nombre,
        String descripcion,
        PlantillaRequestDTO.TipoUso tipoUso,
        PlantillaRequestDTO.Formato formato,
        List<String> tags,
        PlantillaRequestDTO.Origen origen,
        List<CapaDTO> capas,
        LocalDateTime fechaCreacion,
        Boolean activa
) {}