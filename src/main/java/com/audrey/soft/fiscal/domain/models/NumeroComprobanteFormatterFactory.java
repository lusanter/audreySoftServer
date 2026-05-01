package com.audrey.soft.fiscal.domain.models;

/**
 * Fábrica de {@link NumeroComprobanteFormatter} según el sistema tributario.
 * <p>
 * Sistemas soportados:
 * <ul>
 *   <li>{@code "SUNAT"} → {@link SunatFormatter} (correlativo 8 dígitos)</li>
 *   <li>{@code "SRI"}   → {@link SriFormatter}   (correlativo 9 dígitos)</li>
 * </ul>
 * <p>
 * Los sistemas que no generan comprobante numerado (ej. {@code "INTERNO"}) no están
 * soportados; el llamador debe verificar si el sistema requiere número de comprobante
 * antes de invocar este método.
 */
public class NumeroComprobanteFormatterFactory {

    private NumeroComprobanteFormatterFactory() {
        // clase utilitaria, no instanciable
    }

    /**
     * Retorna el formatter correspondiente al sistema tributario indicado.
     *
     * @param sistemaId identificador del sistema tributario (ej. "SUNAT", "SRI")
     * @return formatter apropiado para el sistema
     * @throws IllegalArgumentException si el sistema no está soportado
     */
    public static NumeroComprobanteFormatter forSistema(String sistemaId) {
        if (sistemaId == null) {
            throw new IllegalArgumentException(
                    "Sistema tributario no soportado para formato de comprobante: null");
        }
        switch (sistemaId) {
            case "SUNAT":
                return new SunatFormatter();
            case "SRI":
                return new SriFormatter();
            default:
                throw new IllegalArgumentException(
                        "Sistema tributario no soportado para formato de comprobante: " + sistemaId);
        }
    }
}
