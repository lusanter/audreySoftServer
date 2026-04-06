package com.audrey.soft.inventory.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Categoria {
    private UUID id;
    private UUID sucursalId;
    private String nombre;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Categoria(UUID id, UUID sucursalId, String nombre, boolean active,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.nombre = nombre;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
