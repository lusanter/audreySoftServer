package com.audrey.soft.fiscal.domain.models;

/**
 * Formateador de número de comprobante para el sistema tributario SUNAT (Perú).
 * Formato: serie + "-" + correlativo con padding de 8 dígitos.
 * Ejemplo: B001-00000001
 */
public class SunatFormatter implements NumeroComprobanteFormatter {

    @Override
    public String format(String serie, int correlativo) {
        return serie + "-" + String.format("%08d", correlativo);
    }
}
