package com.audrey.soft.restaurant.infrastructure.persistence.entities;

import com.audrey.soft.restaurant.domain.models.EstadoItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comanda_items", schema = "restaurant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComandaItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comanda_id", nullable = false)
    private ComandaEntity comanda;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @Column(nullable = false, precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal cantidad = BigDecimal.ONE;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(length = 255)
    private String notas;

    @Column(name = "sub_cuenta", length = 10)
    private String subCuenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoItem estado = EstadoItem.PENDIENTE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
