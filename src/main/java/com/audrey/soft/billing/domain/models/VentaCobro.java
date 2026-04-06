package com.audrey.soft.billing.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class VentaCobro {
    private UUID id;
    private UUID ventaId;
    private UUID metodoCobro;
    private BigDecimal monto;
    private String referencia;
    private LocalDateTime createdAt;

    public VentaCobro(UUID id, UUID ventaId, UUID metodoCobro, BigDecimal monto,
                      String referencia, LocalDateTime createdAt) {
        this.id = id;
        this.ventaId = ventaId;
        this.metodoCobro = metodoCobro;
        this.monto = monto;
        this.referencia = referencia;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVentaId() { return ventaId; }
    public void setVentaId(UUID ventaId) { this.ventaId = ventaId; }
    public UUID getMetodoCobro() { return metodoCobro; }
    public void setMetodoCobro(UUID metodoCobro) { this.metodoCobro = metodoCobro; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
