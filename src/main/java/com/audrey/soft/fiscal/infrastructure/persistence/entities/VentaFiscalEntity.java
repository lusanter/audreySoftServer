package com.audrey.soft.fiscal.infrastructure.persistence.entities;

import com.audrey.soft.billing.infrastructure.persistence.entities.VentaEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "venta_fiscal", schema = "fiscal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VentaFiscalEntity {

    @Id
    @Column(name = "venta_id")
    private UUID ventaId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "venta_id")
    private VentaEntity venta;

    @Column(name = "fiscal_sistema_id", nullable = false, length = 20)
    private String fiscalSistemaId;

    @Column(nullable = false)
    @Builder.Default
    private boolean enviado = false;

    @Column(name = "enviado_at")
    private LocalDateTime enviadoAt;

    private Boolean aceptado;

    @Column(name = "aceptado_at")
    private LocalDateTime aceptadoAt;

    @Column(name = "codigo_respuesta", length = 50)
    private String codigoRespuesta;

    @Column(name = "mensaje_respuesta", columnDefinition = "text")
    private String mensajeRespuesta;

    @Column(name = "xml_firmado", columnDefinition = "text")
    private String xmlFirmado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String extra;
}
