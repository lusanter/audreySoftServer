package com.audrey.soft.restaurant.app.usecases.Mesa;

import com.audrey.soft.restaurant.app.dtos.MesaDTO;
import com.audrey.soft.restaurant.app.mappers.MesaMapper;
import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.domain.models.Mesa;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;

import java.util.UUID;

public class CreateMesaUseCase {
    private final MesaRepositoryPort mesaRepository;
    private final MesaMapper mesaMapper;

    public CreateMesaUseCase(MesaRepositoryPort mesaRepository, MesaMapper mesaMapper) {
        this.mesaRepository = mesaRepository;
        this.mesaMapper = mesaMapper;
    }

    public MesaDTO execute(UUID sucursalId, MesaDTO request) {
        if (mesaRepository.existsByNumeroAndSucursalId(request.numero(), sucursalId))
            throw new IllegalArgumentException("Ya existe la mesa N° " + request.numero() + " en esta sucursal");
        Mesa nueva = new Mesa(null, sucursalId, request.numero(),
                request.capacidad() > 0 ? request.capacidad() : 4,
                EstadoMesa.LIBRE, true,
                request.zona() != null && !request.zona().isBlank() ? request.zona() : "GENERAL",
                null, null);
        return mesaMapper.toDto(mesaRepository.save(nueva));
    }
}
