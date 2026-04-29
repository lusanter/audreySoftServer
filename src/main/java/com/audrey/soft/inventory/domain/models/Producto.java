package com.audrey.soft.inventory.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Producto {
    private UUID id;
    private UUID sucursalId;
    private UUID categoriaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioCosto;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private String unidad;
    private boolean active;
    private boolean controlStock;
    private String imagenUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Producto(UUID id, UUID sucursalId, UUID categoriaId, String nombre, String descripcion,
                    BigDecimal precio, BigDecimal stockActual, BigDecimal stockMinimo,
                    String unidad, boolean active, boolean controlStock, BigDecimal precioCosto,
                    String imagenUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.categoriaId = categoriaId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.precioCosto = precioCosto != null ? precioCosto : BigDecimal.ZERO;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.unidad = unidad;
        this.active = active;
        this.controlStock = controlStock;
        this.imagenUrl = imagenUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal precioCosto) { this.precioCosto = precioCosto; }
    public BigDecimal getStockActual() { return stockActual; }
    public void setStockActual(BigDecimal stockActual) { this.stockActual = stockActual; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isControlStock() { return controlStock; }
    public void setControlStock(boolean controlStock) { this.controlStock = controlStock; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
