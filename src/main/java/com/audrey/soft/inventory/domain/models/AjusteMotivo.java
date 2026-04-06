package com.audrey.soft.inventory.domain.models;

import java.util.UUID;

public class AjusteMotivo {
    private UUID id;
    private UUID sucursalId;
    private String nombre;
    private String tipo; // INCREMENTO | DECREMENTO
    private boolean active;

    public AjusteMotivo() {}

    public AjusteMotivo(UUID id, UUID sucursalId, String nombre, String tipo, boolean active) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.active = active;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getSucursalId() { return sucursalId; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public boolean isActive() { return active; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setActive(boolean active) { this.active = active; }
}
