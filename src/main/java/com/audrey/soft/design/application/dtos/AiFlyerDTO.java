package com.audrey.soft.design.application.dtos;

import java.util.List;

/**
 * Espejo del AiFlyerDTO del MS de IA.
 * El Core lo recibe y lo reenvía al frontend sin transformación.
 */
public record AiFlyerDTO(
        List<LayerDTO> layers,
        String formato,
        String descripcion
) {
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
            boolean applyCircularMask
    ) {}
}
