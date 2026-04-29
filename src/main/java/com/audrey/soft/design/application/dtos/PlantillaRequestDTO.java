package com.audrey.soft.design.application.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlantillaRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,
        
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String descripcion,
        
        @NotNull(message = "El tipo de uso es obligatorio")
        TipoUso tipoUso,
        
        @NotNull(message = "El formato es obligatorio")
        Formato formato,
        
        @NotNull(message = "Las tags son obligatorias")
        List<String> tags,
        
        @NotNull(message = "El origen es obligatorio")
        Origen origen,
        
        @NotEmpty(message = "Debe tener al menos una capa")
        @Valid
        List<CapaDTO> capas
) {
    public enum TipoUso {
        IMPULSAR, OFERTA, NUEVO_PRODUCTO, EVENTO
    }
    
    public enum Formato {
        NINE_SIXTEEN("9:16"),
        ONE_ONE("1:1"),
        SIXTEEN_NINE("16:9"),
        FOUR_FIVE("4:5");
        
        private final String value;
        
        Formato(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    public enum Origen {
        SUPER_ADMIN, EMPRESA
    }
}