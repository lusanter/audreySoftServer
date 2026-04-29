package com.audrey.soft.billing.infrastructure.persistence.entities;

import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventas", schema = "billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private SucursalEntity sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_serie_id")
    private ComprobanteSerieEntity comprobanteSerie;

    @OneToOne(mappedBy = "venta", fetch = FetchType.LAZY, optional = true)
    private VentaOrigenEntity origen;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    @Builder.Default
    private String tipoComprobante = "NOTA_VENTA";

    @Column(length = 4)
    private String serie;

    private Integer correlativo;

    @Column(name = "numero_comprobante", length = 20)
    private String numeroComprobante;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "COBRADA";

    @Column(name = "sunat_enviado", nullable = false)
    @Builder.Default
    private boolean sunatEnviado = false;

    @Column(name = "sunat_aceptado")
    private Boolean sunatAceptado;

    @Column(name = "sunat_codigo_hash", length = 100)
    private String sunatCodigoHash;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaItemEntity> items;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaCobroEntity> cobros;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "anulada_at")
    private LocalDateTime anuladaAt;
}
