package com.audrey.soft.fiscal.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fiscal_config", schema = "fiscal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FiscalConfigEntity {

    @Id
    @Column(name = "sucursal_id")
    private UUID sucursalId;

    @Column(name = "fiscal_sistema_id", nullable = false, length = 20)
    private String fiscalSistemaId;

    @Column(name = "moneda_codigo", nullable = false, length = 3)
    @Builder.Default
    private String monedaCodigo = "PEN";

    @Column(name = "ruc_empresa", length = 20)
    private String rucEmpresa;

    @Column(name = "razon_social", length = 200)
    private String razonSocial;

    @Column(name = "direccion_fiscal", length = 300)
    private String direccionFiscal;

    @Column(name = "impuestos_default", columnDefinition = "varchar(20)[]")
    private String[] impuestosDefault;

    @Column(name = "precios_incluyen_impuesto", nullable = false)
    @Builder.Default
    private boolean preciosIncluyenImpuesto = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
