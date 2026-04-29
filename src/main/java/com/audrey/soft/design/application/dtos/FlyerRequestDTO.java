package com.audrey.soft.design.application.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FlyerRequestDTO(
        ProductInfoDTO productInfo,          // legacy — nullable si productos presente
        List<ProductInfoDTO> productos,      // nuevo — 1–3 elementos
        @NotNull PaletteDTO palette,
        String formato,
        String plantillaId,
        String tipoUso
) {
    public FlyerRequestDTO {
        if (formato == null || formato.isBlank()) formato = "9:16";
        if (tipoUso == null || tipoUso.isBlank()) tipoUso = "IMPULSAR";
    }

    /**
     * Normaliza el request: si viene el nuevo formato (productos[]), lo usa.
     * Si viene el formato legacy (productInfo singular), lo convierte a lista.
     * Si ambos son null, lanza excepción.
     */
    public List<ProductInfoDTO> productosNormalizados() {
        if (productos != null && !productos.isEmpty()) return productos;
        if (productInfo != null) return List.of(productInfo);
        throw new IllegalArgumentException("Se requiere al menos un producto");
    }

    public record ProductInfoDTO(String nombre, double precio, String urlImagen) {}
    public record PaletteDTO(String principal, String secundario, String contraste) {}
}
