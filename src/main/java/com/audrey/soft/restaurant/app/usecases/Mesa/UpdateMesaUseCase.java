package com.audrey.soft.restaurant.app.usecases.Mesa;

import com.audrey.soft.restaurant.app.dtos.MesaDTO;
import com.audrey.soft.restaurant.app.mappers.MesaMapper;
import com.audrey.soft.restaurant.domain.models.Mesa;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;

import java.util.UUID;

public class UpdateMesaUseCase {
    private final MesaRepositoryPort mesaRepository;
    private final MesaMapper mesaMapper;

    public UpdateMesaUseCase(MesaRepositoryPort mesaRepository, MesaMapper mesaMapper) {
        this.mesaRepository = mesaRepository;
        this.mesaMapper = mesaMapper;
    }

    public MesaDTO execute(UUID mesaId, MesaDTO request) {
        Mesa existing = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + mesaId));
        existing.setNumero(request.numero());
        existing.setCapacidad(request.capacidad() > 0 ? request.capacidad() : existing.getCapacidad());
        existing.setZona(request.zona() != null && !request.zona().isBlank() ? request.zona() : existing.getZona());
        existing.setActive(request.active());
        return mesaMapper.toDto(mesaRepository.save(existing));
    }
}
