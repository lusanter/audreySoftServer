package com.audrey.soft.fiscal.infrastructure.persistence.entities;

import com.audrey.soft.billing.infrastructure.persistence.entities.VentaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venta_impuestos", schema = "billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VentaImpuestoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "venta_id", nullable = false)
    private UUID ventaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", insertable = false, updatable = false)
    private VentaEntity venta;

    @Column(name = "impuesto_id", nullable = false, length = 20)
    private String impuestoId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 7, scale = 4)
    private BigDecimal tasa;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal base;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "incluido_precio", nullable = false)
    @Builder.Default
    private boolean incluidoPrecio = true;
}
