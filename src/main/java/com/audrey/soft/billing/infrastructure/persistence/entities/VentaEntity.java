package com.audrey.soft.billing.infrastructure.persistence.entities;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.ComprobanteSerieEntity;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.VentaFiscalEntity;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.VentaImpuestoEntity;
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

    @Column(length = 20)
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

    @Column(name = "base_imponible", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal baseImponible = BigDecimal.ZERO;

    @Column(name = "total_impuestos", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalImpuestos = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "COBRADA";

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaImpuestoEntity> impuestos;

    @OneToOne(mappedBy = "venta", fetch = FetchType.LAZY, optional = true)
    private VentaFiscalEntity fiscal;

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
