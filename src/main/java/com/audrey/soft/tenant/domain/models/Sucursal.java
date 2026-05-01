package com.audrey.soft.tenant.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;
import com.audrey.soft.tenant.domain.models.VerticalType;

public class Sucursal {

    private UUID id;
    private UUID empresaId;
    private String nombre;
    private String direccion;
    private String imagenUrl;
    private VerticalType vertical;
    private boolean active;
    private String paisCodigo;
    private String monedaCodigo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Sucursal(UUID id, UUID empresaId, String nombre, String direccion, String imagenUrl,
                    VerticalType vertical, boolean active, String paisCodigo, String monedaCodigo,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.empresaId = empresaId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.imagenUrl = imagenUrl;
        this.vertical = vertical;
        this.active = active;
        this.paisCodigo = paisCodigo;
        this.monedaCodigo = monedaCodigo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmpresaId() { return empresaId; }
    public void setEmpresaId(UUID empresaId) { this.empresaId = empresaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public VerticalType getVertical() { return vertical; }
    public void setVertical(VerticalType vertical) { this.vertical = vertical; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getPaisCodigo() { return paisCodigo; }
    public void setPaisCodigo(String paisCodigo) { this.paisCodigo = paisCodigo; }
    public String getMonedaCodigo() { return monedaCodigo; }
    public void setMonedaCodigo(String monedaCodigo) { this.monedaCodigo = monedaCodigo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
