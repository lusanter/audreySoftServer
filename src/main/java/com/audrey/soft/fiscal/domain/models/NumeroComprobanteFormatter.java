package com.audrey.soft.fiscal.domain.models;

/**
 * Formatea el número de comprobante (serie + correlativo) según el sistema tributario.
 */
public interface NumeroComprobanteFormatter {

    /**
     * Genera el número de comprobante completo.
     *
     * @param serie      serie del comprobante (ej. "B001" para SUNAT, "001-001" para SRI)
     * @param correlativo número correlativo
     * @return número de comprobante formateado (ej. "B001-00000001")
     */
    String format(String serie, int correlativo);
}
