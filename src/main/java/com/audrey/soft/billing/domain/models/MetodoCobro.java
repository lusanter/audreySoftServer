package com.audrey.soft.billing.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class MetodoCobro {
    private UUID id;
    private UUID sucursalId;
    private String nombre;
    private String codigo;
    private String imagenUrl;
    private boolean activo;
    private LocalDateTime createdAt;

    public MetodoCobro(UUID id, UUID sucursalId, String nombre, String codigo,
                       String imagenUrl, boolean activo, LocalDateTime createdAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.nombre = nombre;
        this.codigo = codigo;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
