package com.audrey.soft.fiscal.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "impuesto_tipos", schema = "fiscal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImpuestoTipoEntity {

    @Id
    @Column(length = 20)
    private String id;

    @Column(name = "fiscal_sistema_id", nullable = false, length = 20)
    private String fiscalSistemaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(name = "tasa_default", nullable = false, precision = 7, scale = 4)
    private BigDecimal tasaDefault;

    @Column(name = "tipo_calculo", nullable = false, length = 20)
    @Builder.Default
    private String tipoCalculo = "PORCENTAJE";

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
