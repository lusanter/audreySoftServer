package com.audrey.soft.restaurant.domain.ports;

import com.audrey.soft.restaurant.domain.models.Comanda;
import com.audrey.soft.restaurant.domain.models.EstadoComanda;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComandaRepositoryPort {
    Comanda save(Comanda comanda);
    Optional<Comanda> findById(UUID id);
    List<Comanda> findBySucursalId(UUID sucursalId);
    List<Comanda> findBySucursalIdAndEstado(UUID sucursalId, EstadoComanda estado);
    Optional<Comanda> findOpenByMesaId(UUID mesaId);
}
