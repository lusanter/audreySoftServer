package com.audrey.soft.restaurant.app.usecases.Comanda;

import com.audrey.soft.restaurant.app.dtos.ComandaDTO;
import com.audrey.soft.restaurant.app.mappers.ComandaMapper;
import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListComandasUseCase {
    private final ComandaRepositoryPort comandaRepository;
    private final ComandaMapper comandaMapper;

    public ListComandasUseCase(ComandaRepositoryPort comandaRepository, ComandaMapper comandaMapper) {
        this.comandaRepository = comandaRepository;
        this.comandaMapper = comandaMapper;
    }

    // Devuelve comandas activas (no cerradas ni canceladas)
    public List<ComandaDTO> execute(UUID sucursalId) {
        return comandaRepository.findBySucursalId(sucursalId).stream()
                .filter(c -> c.getEstado() != EstadoComanda.CERRADA && c.getEstado() != EstadoComanda.CANCELADA)
                .map(comandaMapper::toDto)
                .toList();
    }
}
