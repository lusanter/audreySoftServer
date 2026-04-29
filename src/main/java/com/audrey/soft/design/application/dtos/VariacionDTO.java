package com.audrey.soft.design.application.dtos;

import java.util.List;

/**
 * Una variación individual del flyer generado.
 * Cada variación usa una plantilla distinta para garantizar diversidad visual.
 */
public record VariacionDTO(
        int variacionId,
        String plantillaNombre,
        List<LayerDTO> layers,
        String formato,
        String descripcion
) {
    /**
     * Capa del flyer con coordenadas relativas [0.0–1.0] y propiedades tipográficas.
     */
    public record LayerDTO(
            String tipo,
            String url,
            String text,
            String colorHex,
            double x,
            double y,
            double width,
            double height,
            int zIndex,
            boolean applyCircularMask,
            String fontFamily,
            Integer fontSize,
            String fontWeight,
            String textAlign
    ) {}
}
