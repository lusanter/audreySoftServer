package com.audrey.soft.inventory.domain.ports;

import com.audrey.soft.inventory.app.dtos.StockMovementDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockMovementRepositoryPort {
    List<StockMovementDTO> findBySucursalId(UUID sucursalId);
    void save(UUID productoId, String tipo, BigDecimal cantidad, BigDecimal precioCosto, UUID referenciaId, UUID motivoId, String nota, LocalDateTime createdAt);
}
