package com.audrey.soft.fiscal.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fiscal_sistemas", schema = "fiscal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FiscalSistemaEntity {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "pais_codigo", nullable = false, length = 3)
    private String paisCodigo;

    @Column(name = "moneda_default", nullable = false, length = 3)
    private String monedaDefault;

    @Column(name = "serie_formato", length = 50)
    private String serieFormato;

    @Column(name = "serie_regex", length = 100)
    private String serieRegex;

    @Column(name = "correlativo_padding", nullable = false)
    @Builder.Default
    private int correlativoPadding = 8;

    @Column(nullable = false, length = 5)
    @Builder.Default
    private String separador = "-";

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
