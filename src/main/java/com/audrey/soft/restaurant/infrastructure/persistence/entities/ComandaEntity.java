package com.audrey.soft.restaurant.infrastructure.persistence.entities;

import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comandas", schema = "restaurant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComandaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private SucursalEntity sucursal;

    @Column(name = "mesa_id")
    private UUID mesaId;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoComanda estado = EstadoComanda.ABIERTA;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(columnDefinition = "text")
    private String notas;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaItemEntity> items;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
