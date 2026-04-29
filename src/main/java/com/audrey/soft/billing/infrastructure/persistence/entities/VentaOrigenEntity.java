package com.audrey.soft.billing.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "venta_origen", schema = "billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VentaOrigenEntity {

    @Id
    private UUID ventaId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "venta_id")
    private VentaEntity venta;

    @Column(nullable = false, length = 30)
    private String tipoOrigen;

    @Column(nullable = false)
    private UUID origenId;
}
