package com.audrey.soft.fiscal.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComprobanteSerie {
    private UUID id;
    private UUID sucursalId;
    private String tipoComprobante;   // BOLETA | FACTURA | NOTA_VENTA
    private String serie;             // B001, F001, NV01
    private int correlativoActual;
    private int correlativoMax;
    private boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComprobanteSerie(UUID id, UUID sucursalId, String tipoComprobante, String serie,
                            int correlativoActual, int correlativoMax, boolean activo,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.tipoComprobante = tipoComprobante;
        this.serie = serie;
        this.correlativoActual = correlativoActual;
        this.correlativoMax = correlativoMax;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public int getCorrelativoActual() { return correlativoActual; }
    public void setCorrelativoActual(int correlativoActual) { this.correlativoActual = correlativoActual; }
    public int getCorrelativoMax() { return correlativoMax; }
    public void setCorrelativoMax(int correlativoMax) { this.correlativoMax = correlativoMax; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
