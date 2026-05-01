package com.audrey.soft.fiscal.domain.models;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.ImpuestoTipoEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de dominio puro (sin anotaciones Spring) que calcula las líneas
 * de impuesto para una venta dado un conjunto de tipos de impuesto y la
 * configuración fiscal de la sucursal.
 *
 * <p>Fórmulas:
 * <ul>
 *   <li>Precio incluye impuesto: {@code monto = base * tasa / (1 + tasa)}</li>
 *   <li>Precio NO incluye impuesto: {@code monto = base * tasa}</li>
 * </ul>
 * Todas las divisiones usan {@link RoundingMode#HALF_UP} con escala 2.
 */
public class ImpuestoCalculator {

    private ImpuestoCalculator() {
        // Clase utilitaria — no instanciar
    }

    /**
     * Calcula las líneas de impuesto para una venta.
     *
     * @param base                   monto base sobre el que se aplican los impuestos
     * @param impuestos              lista de tipos de impuesto del catálogo fiscal
     * @param preciosIncluyenImpuesto {@code true} si el precio ya incluye el impuesto
     * @return lista de {@link VentaImpuesto} con snapshot de cada impuesto calculado
     */
    public static List<VentaImpuesto> calcular(
            BigDecimal base,
            List<ImpuestoTipoEntity> impuestos,
            boolean preciosIncluyenImpuesto) {

        List<VentaImpuesto> resultado = new ArrayList<>();

        if (impuestos == null || impuestos.isEmpty()) {
            return resultado;
        }

        for (ImpuestoTipoEntity impuesto : impuestos) {
            BigDecimal tasa = impuesto.getTasaDefault();
            BigDecimal monto;

            if (preciosIncluyenImpuesto) {
                // monto = base * tasa / (1 + tasa)
                BigDecimal divisor = BigDecimal.ONE.add(tasa);
                monto = base.multiply(tasa)
                            .divide(divisor, 2, RoundingMode.HALF_UP);
            } else {
                // monto = base * tasa
                monto = base.multiply(tasa)
                            .setScale(2, RoundingMode.HALF_UP);
            }

            VentaImpuesto ventaImpuesto = new VentaImpuesto(
                    null,                       // id — se asigna al persistir
                    null,                       // ventaId — se asigna al persistir
                    impuesto.getId(),           // impuestoId (referencia al catálogo)
                    impuesto.getCodigo(),       // snapshot: código
                    impuesto.getNombre(),       // snapshot: nombre
                    tasa,                       // snapshot: tasa
                    base,                       // base imponible
                    monto,                      // monto calculado
                    preciosIncluyenImpuesto     // flag incluido en precio
            );

            resultado.add(ventaImpuesto);
        }

        return resultado;
    }
}
