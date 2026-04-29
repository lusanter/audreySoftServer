package com.audrey.soft.design.infrastructure.persistence.entities;

import com.audrey.soft.design.application.dtos.CapaDTO;
import com.audrey.soft.design.application.dtos.PlantillaRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "plantillas", schema = "design")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaEntity {
    
    @Id
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_uso", nullable = false)
    private PlantillaRequestDTO.TipoUso tipoUso;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantillaRequestDTO.Formato formato;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> tags;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantillaRequestDTO.Origen origen;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private List<CapaDTO> capas;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(nullable = false)
    private Boolean activa;
    
    @Column(name = "empresa_id")
    private UUID empresaId;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activa == null) {
            activa = true;
        }
    }
}