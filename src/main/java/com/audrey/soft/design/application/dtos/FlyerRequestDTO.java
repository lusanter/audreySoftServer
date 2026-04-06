package com.audrey.soft.design.application.dtos;

import jakarta.validation.constraints.NotNull;

public record FlyerRequestDTO(
        @NotNull ProductInfoDTO productInfo,
        @NotNull PaletteDTO palette,
        String formato
) {
    public FlyerRequestDTO {
        if (formato == null || formato.isBlank()) formato = "9:16";
    }

    public record ProductInfoDTO(String nombre, double precio, String urlImagen) {}
    public record PaletteDTO(String principal, String secundario, String contraste) {}
}
