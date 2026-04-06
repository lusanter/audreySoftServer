package com.audrey.soft.restaurant.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ComandaItem {
    private UUID id;
    private UUID comandaId;
    private UUID productoId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private String notas;
    private String subCuenta;
    private EstadoItem estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComandaItem(UUID id, UUID comandaId, UUID productoId, BigDecimal cantidad,
                       BigDecimal precioUnitario, String notas, String subCuenta, EstadoItem estado,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.comandaId = comandaId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.notas = notas;
        this.subCuenta = subCuenta;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }
    public UUID getProductoId() { return productoId; }
    public void setProductoId(UUID productoId) { this.productoId = productoId; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public String getSubCuenta() { return subCuenta; }
    public void setSubCuenta(String subCuenta) { this.subCuenta = subCuenta; }
    public EstadoItem getEstado() { return estado; }
    public void setEstado(EstadoItem estado) { this.estado = estado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
