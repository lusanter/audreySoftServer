package com.audrey.soft.billing.domain.models;

import java.util.UUID;

public class VentaOrigen {

    private final String tipoOrigen;
    private final UUID origenId;

    public VentaOrigen(String tipoOrigen, UUID origenId) {
        if (tipoOrigen == null || origenId == null) {
            throw new IllegalArgumentException("tipoOrigen y origenId no pueden ser nulos");
        }
        this.tipoOrigen = tipoOrigen;
        this.origenId = origenId;
    }

    public String getTipoOrigen() {
        return tipoOrigen;
    }

    public UUID getOrigenId() {
        return origenId;
    }
}
