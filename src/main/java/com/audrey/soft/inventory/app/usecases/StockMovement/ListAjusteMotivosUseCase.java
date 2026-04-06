package com.audrey.soft.inventory.app.usecases.StockMovement;

import com.audrey.soft.inventory.app.dtos.AjusteMotivoDTO;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataAjusteMotivoRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListAjusteMotivosUseCase {

    private final SpringDataAjusteMotivoRepository repo;

    public ListAjusteMotivosUseCase(SpringDataAjusteMotivoRepository repo) {
        this.repo = repo;
    }

    public List<AjusteMotivoDTO> execute(UUID sucursalId) {
        return repo.findBySucursalIdAndActiveTrue(sucursalId).stream()
                .map(e -> new AjusteMotivoDTO(e.getId(), e.getNombre(), e.getTipo(), e.isActive()))
                .collect(Collectors.toList());
    }
}
