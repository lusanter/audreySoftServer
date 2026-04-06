package com.audrey.soft.billing.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Venta {
    private UUID id;
    private UUID sucursalId;
    private UUID comprobanteSerieId;
    private UUID comandaId;
    private UUID clienteId;
    private String tipoComprobante;
    private String serie;
    private Integer correlativo;
    private String numeroComprobante;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal total;
    private List<VentaItem> items;
    private List<VentaCobro> cobros;
    private LocalDateTime createdAt;

    public Venta(UUID id, UUID sucursalId, UUID comprobanteSerieId, UUID comandaId, UUID clienteId,
                 String tipoComprobante, String serie, Integer correlativo, String numeroComprobante,
                 BigDecimal subtotal, BigDecimal descuento, BigDecimal igv, BigDecimal total,
                 List<VentaItem> items, List<VentaCobro> cobros, LocalDateTime createdAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.comprobanteSerieId = comprobanteSerieId;
        this.comandaId = comandaId;
        this.clienteId = clienteId;
        this.tipoComprobante = tipoComprobante;
        this.serie = serie;
        this.correlativo = correlativo;
        this.numeroComprobante = numeroComprobante;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.igv = igv;
        this.total = total;
        this.items = items;
        this.cobros = cobros;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public UUID getComprobanteSerieId() { return comprobanteSerieId; }
    public void setComprobanteSerieId(UUID comprobanteSerieId) { this.comprobanteSerieId = comprobanteSerieId; }
    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public Integer getCorrelativo() { return correlativo; }
    public void setCorrelativo(Integer correlativo) { this.correlativo = correlativo; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<VentaItem> getItems() { return items; }
    public void setItems(List<VentaItem> items) { this.items = items; }
    public List<VentaCobro> getCobros() { return cobros; }
    public void setCobros(List<VentaCobro> cobros) { this.cobros = cobros; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
