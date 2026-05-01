package com.audrey.soft.billing.domain.models;

import com.audrey.soft.fiscal.domain.models.VentaImpuesto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Venta {
    private UUID id;
    private UUID sucursalId;
    private UUID comprobanteSerieId;
    private VentaOrigen origen;
    private UUID clienteId;
    private String tipoComprobante;
    private String serie;
    private Integer correlativo;
    private String numeroComprobante;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal baseImponible;
    private BigDecimal totalImpuestos;
    private BigDecimal total;
    private String estado;
    private boolean fiscalEnviado;
    private String fiscalSistemaId;
    private List<VentaItem> items;
    private List<VentaCobro> cobros;
    private List<VentaImpuesto> impuestos;
    private LocalDateTime createdAt;

    public Venta(UUID id, UUID sucursalId, UUID comprobanteSerieId, VentaOrigen origen, UUID clienteId,
                 String tipoComprobante, String serie, Integer correlativo, String numeroComprobante,
                 BigDecimal subtotal, BigDecimal descuento, BigDecimal baseImponible, BigDecimal totalImpuestos,
                 BigDecimal total, String estado, boolean fiscalEnviado, String fiscalSistemaId,
                 List<VentaItem> items, List<VentaCobro> cobros, List<VentaImpuesto> impuestos,
                 LocalDateTime createdAt) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.comprobanteSerieId = comprobanteSerieId;
        this.origen = origen;
        this.clienteId = clienteId;
        this.tipoComprobante = tipoComprobante;
        this.serie = serie;
        this.correlativo = correlativo;
        this.numeroComprobante = numeroComprobante;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.baseImponible = baseImponible;
        this.totalImpuestos = totalImpuestos;
        this.total = total;
        this.estado = estado;
        this.fiscalEnviado = fiscalEnviado;
        this.fiscalSistemaId = fiscalSistemaId;
        this.items = items;
        this.cobros = cobros;
        this.impuestos = impuestos;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSucursalId() { return sucursalId; }
    public void setSucursalId(UUID sucursalId) { this.sucursalId = sucursalId; }
    public UUID getComprobanteSerieId() { return comprobanteSerieId; }
    public void setComprobanteSerieId(UUID comprobanteSerieId) { this.comprobanteSerieId = comprobanteSerieId; }
    public VentaOrigen getOrigen() { return origen; }
    public void setOrigen(VentaOrigen origen) { this.origen = origen; }
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
    public BigDecimal getBaseImponible() { return baseImponible; }
    public void setBaseImponible(BigDecimal baseImponible) { this.baseImponible = baseImponible; }
    public BigDecimal getTotalImpuestos() { return totalImpuestos; }
    public void setTotalImpuestos(BigDecimal totalImpuestos) { this.totalImpuestos = totalImpuestos; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isFiscalEnviado() { return fiscalEnviado; }
    public void setFiscalEnviado(boolean fiscalEnviado) { this.fiscalEnviado = fiscalEnviado; }
    public String getFiscalSistemaId() { return fiscalSistemaId; }
    public void setFiscalSistemaId(String fiscalSistemaId) { this.fiscalSistemaId = fiscalSistemaId; }
    public List<VentaItem> getItems() { return items; }
    public void setItems(List<VentaItem> items) { this.items = items; }
    public List<VentaCobro> getCobros() { return cobros; }
    public void setCobros(List<VentaCobro> cobros) { this.cobros = cobros; }
    public List<VentaImpuesto> getImpuestos() { return impuestos; }
    public void setImpuestos(List<VentaImpuesto> impuestos) { this.impuestos = impuestos; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
