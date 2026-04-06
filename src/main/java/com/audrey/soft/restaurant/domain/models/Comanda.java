package com.audrey.soft.restaurant.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Comanda {
    private UUID id;
    private UUID sucursalId;
    private UUID mesaId;
    private UUID clienteId;
    private EstadoComanda estado;
    private BigDecimal total;
    private String notas;
    private List<ComandaItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    public Comanda(UUID id, UUID sucursalId, UUID mesaId, UUID clienteId,
                   EstadoComanda estado, BigDecimal total, String notas,
                   List<ComandaItem> items, LocalDateTime createdAt, LocalDateTime closedAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.mesaId = mesaId;
        this.clienteId = clienteId;
        this.estado = estado;
        this.total = total;
        this.notas = notas;
        this.items = items;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public UUID getMesaId() { return mesaId; }
    public void setMesaId(UUID mesaId) { this.mesaId = mesaId; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public EstadoComanda getEstado() { return estado; }
    public void setEstado(EstadoComanda estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public List<ComandaItem> getItems() { return items; }
    public void setItems(List<ComandaItem> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
