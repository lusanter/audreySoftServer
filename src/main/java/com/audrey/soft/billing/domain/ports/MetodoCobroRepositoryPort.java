package com.audrey.soft.billing.domain.ports;

import com.audrey.soft.billing.domain.models.MetodoCobro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetodoCobroRepositoryPort {
    MetodoCobro save(MetodoCobro metodoCobro);
    Optional<MetodoCobro> findById(UUID id);
    List<MetodoCobro> findDisponiblesBySucursal(UUID sucursalId);
}
