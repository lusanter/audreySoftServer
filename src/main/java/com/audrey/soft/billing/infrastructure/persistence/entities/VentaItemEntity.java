package com.audrey.soft.billing.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venta_items", schema = "billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VentaItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaEntity venta;

    @Column(name = "producto_id")
    private UUID productoId;

    @Column(name = "nombre_producto", nullable = false, length = 150)
    private String nombreProducto;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento_item", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal descuentoItem = BigDecimal.ZERO;
}
