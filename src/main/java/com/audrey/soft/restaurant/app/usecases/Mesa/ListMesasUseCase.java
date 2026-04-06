package com.audrey.soft.restaurant.app.usecases.Mesa;

import com.audrey.soft.restaurant.app.dtos.MesaDTO;
import com.audrey.soft.restaurant.app.mappers.MesaMapper;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListMesasUseCase {
    private final MesaRepositoryPort mesaRepository;
    private final MesaMapper mesaMapper;

    public ListMesasUseCase(MesaRepositoryPort mesaRepository, MesaMapper mesaMapper) {
        this.mesaRepository = mesaRepository;
        this.mesaMapper = mesaMapper;
    }

    public List<MesaDTO> execute(UUID sucursalId) {
        return mesaRepository.findBySucursalId(sucursalId)
                .stream().map(mesaMapper::toDto).toList();
    }
}
