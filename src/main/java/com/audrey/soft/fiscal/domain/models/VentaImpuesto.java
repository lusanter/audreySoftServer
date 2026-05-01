package com.audrey.soft.fiscal.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Snapshot inmutable de un impuesto aplicado a una venta.
 * Los datos se copian del catálogo fiscal en el momento de la venta;
 * cambios posteriores en el catálogo no afectan registros existentes.
 */
public class VentaImpuesto {
    private UUID id;
    private UUID ventaId;
    private String impuestoId;
    private String codigo;
    private String nombre;
    private BigDecimal tasa;
    private BigDecimal base;
    private BigDecimal monto;
    private boolean incluidoPrecio;

    public VentaImpuesto(UUID id, UUID ventaId, String impuestoId, String codigo, String nombre,
                         BigDecimal tasa, BigDecimal base, BigDecimal monto, boolean incluidoPrecio) {
        this.id = id;
        this.ventaId = ventaId;
        this.impuestoId = impuestoId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tasa = tasa;
        this.base = base;
        this.monto = monto;
        this.incluidoPrecio = incluidoPrecio;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVentaId() { return ventaId; }
    public void setVentaId(UUID ventaId) { this.ventaId = ventaId; }
    public String getImpuestoId() { return impuestoId; }
    public void setImpuestoId(String impuestoId) { this.impuestoId = impuestoId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getTasa() { return tasa; }
    public void setTasa(BigDecimal tasa) { this.tasa = tasa; }
    public BigDecimal getBase() { return base; }
    public void setBase(BigDecimal base) { this.base = base; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public boolean isIncluidoPrecio() { return incluidoPrecio; }
    public void setIncluidoPrecio(boolean incluidoPrecio) { this.incluidoPrecio = incluidoPrecio; }
}
