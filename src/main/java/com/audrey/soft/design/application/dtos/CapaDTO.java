package com.audrey.soft.design.application.dtos;

import jakarta.validation.constraints.*;

public record CapaDTO(
        @NotNull(message = "El tipo de capa es obligatorio")
        LayerType tipo,
        
        @DecimalMin(value = "0.0", message = "X debe ser mayor o igual a 0")
        @DecimalMax(value = "1.0", message = "X debe ser menor o igual a 1")
        double x,
        
        @DecimalMin(value = "0.0", message = "Y debe ser mayor o igual a 0")
        @DecimalMax(value = "1.0", message = "Y debe ser menor o igual a 1")
        double y,
        
        @DecimalMin(value = "0.01", message = "Width debe ser mayor a 0")
        @DecimalMax(value = "1.0", message = "Width debe ser menor o igual a 1")
        double width,
        
        @DecimalMin(value = "0.01", message = "Height debe ser mayor a 0")
        @DecimalMax(value = "1.0", message = "Height debe ser menor o igual a 1")
        double height,
        
        @Min(value = 0, message = "zIndex debe ser mayor o igual a 0")
        int zIndex,
        
        @NotNull(message = "applyCircularMask es obligatorio")
        Boolean applyCircularMask,
        
        ColorHint colorHint,
        
        TextHint textHint
) {
    public enum LayerType {
        BACKGROUND, OVERLAY, PRODUCT, TEXT_TITLE, TEXT_SUBTITLE, TEXT_PRICE, LOGO
    }
    
    public enum ColorHint {
        PRINCIPAL, SECUNDARIO, CONTRASTE
    }
    
    public enum TextHint {
        NOMBRE_PRODUCTO, PRECIO, DESCRIPCION
    }
}