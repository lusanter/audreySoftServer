package com.audrey.soft.inventory.app.usecases.StockMovement;

import com.audrey.soft.inventory.app.dtos.StockEntradaRequest;
import com.audrey.soft.inventory.app.dtos.StockMovementDTO;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateStockEntradaUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final StockMovementRepositoryPort stockMovementRepository;

    public CreateStockEntradaUseCase(ProductoRepositoryPort productoRepository,
                                     StockMovementRepositoryPort stockMovementRepository) {
        this.productoRepository = productoRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public StockMovementDTO execute(StockEntradaRequest request) {
        var producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + request.productoId()));

        if (!producto.isControlStock())
            throw new IllegalStateException("El producto '" + producto.getNombre() + "' no tiene control de stock activo.");

        if (request.cantidad() == null || request.cantidad().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");

        if (request.precioCosto() == null || request.precioCosto().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El precio costo no puede ser negativo.");

        // 1. Registrar el movimiento ENTRADA con el precio costo de esta compra (fijo para siempre)
        LocalDateTime ahora = LocalDateTime.now();
        stockMovementRepository.save(
                request.productoId(),
                "ENTRADA",
                request.cantidad(),
                request.precioCosto(),
                null,                   // referencia_id
                null,                   // motivoId (null para entradas normales)
                request.nota(),
                ahora
        );

        // 2. Actualizar stock_actual += cantidad y precio_costo = nuevo (último precio de compra)
        productoRepository.incrementarStockYActualizarCosto(
                request.productoId(),
                request.cantidad(),
                request.precioCosto()
        );

        // 3. Retornar el DTO del movimiento creado (volvemos a leer el estado actualizado)
        return new StockMovementDTO(
                null, // id generado por BD
                producto.getId(),
                producto.getNombre(),
                "ENTRADA",
                request.cantidad(),
                request.precioCosto(),
                null,
                null, // motivoNombre
                "INCREMENTO",
                request.nota(),
                ahora
        );
    }
}
