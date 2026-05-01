package com.audrey.soft.billing.domain.models;

import com.audrey.soft.fiscal.domain.models.ImpuestoCalculator;
import com.audrey.soft.fiscal.domain.models.VentaImpuesto;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.ImpuestoTipoEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImpuestoCalculatorTest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private ImpuestoTipoEntity buildImpuesto(String id, String codigo, String nombre, String tasa) {
        return ImpuestoTipoEntity.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .tasaDefault(new BigDecimal(tasa))
                .fiscalSistemaId("SUNAT")
                .build();
    }

    // -----------------------------------------------------------------------
    // Test 1: IGV 18% incluido en precio
    //   base=100, tasa=0.18, preciosIncluyenImpuesto=true
    //   monto = 100 * 0.18 / 1.18 = 15.25
    // -----------------------------------------------------------------------
    @Test
    void igv18PorCientoIncluidoEnPrecio_debeCalcular1525() {
        ImpuestoTipoEntity igv = buildImpuesto("IGV", "IGV",
                "Impuesto General a las Ventas", "0.18");

        List<VentaImpuesto> resultado = ImpuestoCalculator.calcular(
                new BigDecimal("100.00"),
                List.of(igv),
                true
        );

        assertEquals(1, resultado.size());
        VentaImpuesto vi = resultado.get(0);
        assertEquals(new BigDecimal("15.25"), vi.getMonto());
        assertEquals(new BigDecimal("100.00"), vi.getBase());
        assertEquals(new BigDecimal("0.18"), vi.getTasa());
        assertEquals("IGV", vi.getCodigo());
        assertEquals("Impuesto General a las Ventas", vi.getNombre());
        assertTrue(vi.isIncluidoPrecio());
    }

    // -----------------------------------------------------------------------
    // Test 2: IVA 19% NO incluido en precio
    //   base=100, tasa=0.19, preciosIncluyenImpuesto=false
    //   monto = 100 * 0.19 = 19.00
    // -----------------------------------------------------------------------
    @Test
    void iva19PorCientoNoIncluido_debeCalcular1900() {
        ImpuestoTipoEntity iva = buildImpuesto("IVA_CO", "IVA",
                "Impuesto al Valor Agregado", "0.19");

        List<VentaImpuesto> resultado = ImpuestoCalculator.calcular(
                new BigDecimal("100.00"),
                List.of(iva),
                false
        );

        assertEquals(1, resultado.size());
        VentaImpuesto vi = resultado.get(0);
        assertEquals(new BigDecimal("19.00"), vi.getMonto());
        assertEquals(new BigDecimal("100.00"), vi.getBase());
        assertEquals(new BigDecimal("0.19"), vi.getTasa());
        assertEquals("IVA", vi.getCodigo());
        assertFalse(vi.isIncluidoPrecio());
    }

    // -----------------------------------------------------------------------
    // Test 3: Lista vacía de impuestos → retorna lista vacía
    // -----------------------------------------------------------------------
    @Test
    void listaVaciaDeImpuestos_debeRetornarListaVacia() {
        List<VentaImpuesto> resultado = ImpuestoCalculator.calcular(
                new BigDecimal("100.00"),
                Collections.emptyList(),
                true
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // -----------------------------------------------------------------------
    // Test 4: Múltiples impuestos → retorna un VentaImpuesto por tipo
    //   IGV 18% + ISC 17%, incluidos en precio, base=100
    // -----------------------------------------------------------------------
    @Test
    void multiplesImpuestos_debeRetornarUnaLineaPorTipo() {
        ImpuestoTipoEntity igv = buildImpuesto("IGV", "IGV",
                "Impuesto General a las Ventas", "0.18");
        ImpuestoTipoEntity isc = buildImpuesto("ISC", "ISC",
                "Impuesto Selectivo al Consumo", "0.17");

        List<VentaImpuesto> resultado = ImpuestoCalculator.calcular(
                new BigDecimal("100.00"),
                List.of(igv, isc),
                true
        );

        assertEquals(2, resultado.size());

        VentaImpuesto lineaIgv = resultado.get(0);
        assertEquals("IGV", lineaIgv.getCodigo());
        assertEquals(new BigDecimal("15.25"), lineaIgv.getMonto()); // 100*0.18/1.18

        VentaImpuesto lineaIsc = resultado.get(1);
        assertEquals("ISC", lineaIsc.getCodigo());
        // 100 * 0.17 / 1.17 = 14.53
        assertEquals(new BigDecimal("14.53"), lineaIsc.getMonto());
    }

    // -----------------------------------------------------------------------
    // Test adicional: snapshot preserva impuestoId del catálogo
    // -----------------------------------------------------------------------
    @Test
    void snapshot_debePreservarImpuestoIdDelCatalogo() {
        ImpuestoTipoEntity igv = buildImpuesto("IGV", "IGV",
                "Impuesto General a las Ventas", "0.18");

        List<VentaImpuesto> resultado = ImpuestoCalculator.calcular(
                new BigDecimal("50.00"),
                List.of(igv),
                false
        );

        assertEquals(1, resultado.size());
        assertEquals("IGV", resultado.get(0).getImpuestoId());
        assertNull(resultado.get(0).getId());       // id se asigna al persistir
        assertNull(resultado.get(0).getVentaId());  // ventaId se asigna al persistir
    }
}
