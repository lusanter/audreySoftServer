package com.audrey.soft.fiscal.domain.models;

/**
 * Formateador de número de comprobante para el sistema tributario SRI (Ecuador).
 * Formato: serie + "-" + correlativo con padding de 9 dígitos.
 * Ejemplo: 001-001-000000001
 */
public class SriFormatter implements NumeroComprobanteFormatter {

    @Override
    public String format(String serie, int correlativo) {
        return serie + "-" + String.format("%09d", correlativo);
    }
}
