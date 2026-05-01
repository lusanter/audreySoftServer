package com.audrey.soft.billing.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeroComprobanteFormatterTest {

    // --- SunatFormatter ---

    @Test
    void sunatFormatter_paddsCorrelativoTo8Digits() {
        NumeroComprobanteFormatter formatter = new SunatFormatter();
        assertEquals("B001-00000001", formatter.format("B001", 1));
    }

    @Test
    void sunatFormatter_largeCorrelativo() {
        NumeroComprobanteFormatter formatter = new SunatFormatter();
        assertEquals("F001-12345678", formatter.format("F001", 12345678));
    }

    // --- SriFormatter ---

    @Test
    void sriFormatter_paddsCorrelativoTo9Digits() {
        NumeroComprobanteFormatter formatter = new SriFormatter();
        assertEquals("001-001-000000001", formatter.format("001-001", 1));
    }

    @Test
    void sriFormatter_largeCorrelativo() {
        NumeroComprobanteFormatter formatter = new SriFormatter();
        assertEquals("001-002-123456789", formatter.format("001-002", 123456789));
    }

    // --- Factory ---

    @Test
    void factory_sunat_returnsSunatFormatter() {
        NumeroComprobanteFormatter formatter = NumeroComprobanteFormatterFactory.forSistema("SUNAT");
        assertInstanceOf(SunatFormatter.class, formatter);
        assertEquals("B001-00000001", formatter.format("B001", 1));
    }

    @Test
    void factory_sri_returnsSriFormatter() {
        NumeroComprobanteFormatter formatter = NumeroComprobanteFormatterFactory.forSistema("SRI");
        assertInstanceOf(SriFormatter.class, formatter);
        assertEquals("001-001-000000001", formatter.format("001-001", 1));
    }

    @Test
    void factory_unknownSystem_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> NumeroComprobanteFormatterFactory.forSistema("DIAN")
        );
        assertTrue(ex.getMessage().contains("DIAN"));
    }

    @Test
    void factory_interno_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> NumeroComprobanteFormatterFactory.forSistema("INTERNO")
        );
        assertTrue(ex.getMessage().contains("INTERNO"));
    }

    @Test
    void factory_null_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> NumeroComprobanteFormatterFactory.forSistema(null)
        );
    }
}
