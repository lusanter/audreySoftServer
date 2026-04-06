package com.audrey.soft.restaurant.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Mesa {
    private UUID id;
    private UUID sucursalId;
    private int numero;
    private int capacidad;
    private EstadoMesa estado;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String zona;

    public Mesa(UUID id, UUID sucursalId, int numero, int capacidad,
                EstadoMesa estado, boolean active, String zona, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
        this.active = active;
        this.zona = zona != null ? zona : "GENERAL";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public EstadoMesa getEstado() { return estado; }
    public void setEstado(EstadoMesa estado) { this.estado = estado; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
