package com.audrey.soft.fiscal.infrastructure.persistence.entities;

import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comprobante_series", schema = "billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComprobanteSerieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private SucursalEntity sucursal;

    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private String tipoComprobante;

    @Column(nullable = false, length = 4)
    private String serie;

    @Column(name = "correlativo_actual", nullable = false)
    @Builder.Default
    private int correlativoActual = 0;

    @Column(name = "correlativo_max", nullable = false)
    @Builder.Default
    private int correlativoMax = 99999999;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
