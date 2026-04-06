package com.audrey.soft.restaurant.infrastructure.persistence.entities;

import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mesas", schema = "restaurant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MesaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private SucursalEntity sucursal;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false)
    @Builder.Default
    private int capacidad = 4;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String zona = "GENERAL";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoMesa estado = EstadoMesa.LIBRE;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
