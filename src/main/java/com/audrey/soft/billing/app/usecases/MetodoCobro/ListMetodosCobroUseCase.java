package com.audrey.soft.billing.app.usecases.MetodoCobro;

import com.audrey.soft.billing.app.dtos.MetodoCobroDTO;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListMetodosCobroUseCase {
    private final MetodoCobroRepositoryPort metodoCobroRepository;

    public ListMetodosCobroUseCase(MetodoCobroRepositoryPort metodoCobroRepository) {
        this.metodoCobroRepository = metodoCobroRepository;
    }

    public List<MetodoCobroDTO> execute(UUID sucursalId) {
        return metodoCobroRepository.findDisponiblesBySucursal(sucursalId).stream()
                .map(m -> new MetodoCobroDTO(m.getId(), m.getSucursalId(), m.getNombre(),
                        m.getCodigo(), m.getImagenUrl(), m.isActivo()))
                .toList();
    }
}
