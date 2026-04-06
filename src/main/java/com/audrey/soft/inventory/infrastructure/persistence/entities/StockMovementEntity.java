package com.audrey.soft.inventory.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements", schema = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(nullable = false, length = 20)
    private String tipo; // ENTRADA | SALIDA | AJUSTE

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(length = 255)
    private String nota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ajuste_motivo_id")
    private AjusteMotivoEntity ajusteMotivo;

    @Column(name = "precio_costo", precision = 12, scale = 2)
    private BigDecimal precioCosto; // null para SALIDA/AJUSTE, valor real para ENTRADA

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
