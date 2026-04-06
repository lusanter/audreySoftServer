package com.audrey.soft.inventory.domain.ports;

import com.audrey.soft.inventory.domain.models.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepositoryPort {
    Producto save(Producto producto);
    Optional<Producto> findById(UUID id);
    List<Producto> findBySucursalId(UUID sucursalId);
    // Para descontar stock al cerrar comanda
    void decrementarStock(UUID productoId, BigDecimal cantidad);
    // Para incrementar stock y actualizar precio costo al registrar una entrada
    void incrementarStockYActualizarCosto(UUID productoId, BigDecimal cantidad, BigDecimal precioCosto);
}
