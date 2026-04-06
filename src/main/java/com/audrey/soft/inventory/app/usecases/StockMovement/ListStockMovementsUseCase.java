package com.audrey.soft.inventory.app.usecases.StockMovement;

import com.audrey.soft.inventory.app.dtos.StockMovementDTO;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListStockMovementsUseCase {

    private final StockMovementRepositoryPort repository;

    public ListStockMovementsUseCase(StockMovementRepositoryPort repository) {
        this.repository = repository;
    }

    public List<StockMovementDTO> execute(UUID sucursalId) {
        return repository.findBySucursalId(sucursalId);
    }
}
