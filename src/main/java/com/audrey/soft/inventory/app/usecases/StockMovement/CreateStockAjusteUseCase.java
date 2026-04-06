package com.audrey.soft.inventory.app.usecases.StockMovement;

import com.audrey.soft.inventory.app.dtos.StockAjusteRequest;
import com.audrey.soft.inventory.app.dtos.StockMovementDTO;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataAjusteMotivoRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateStockAjusteUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final StockMovementRepositoryPort stockMovementRepository;
    private final SpringDataAjusteMotivoRepository motivoRepository;

    public CreateStockAjusteUseCase(ProductoRepositoryPort productoRepository,
                                    StockMovementRepositoryPort stockMovementRepository,
                                    SpringDataAjusteMotivoRepository motivoRepository) {
        this.productoRepository = productoRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.motivoRepository = motivoRepository;
    }

    @Transactional
    public StockMovementDTO execute(StockAjusteRequest request) {
        var producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + request.productoId()));

        var motivo = motivoRepository.findById(request.motivoId())
                .orElseThrow(() -> new RuntimeException("Motivo de ajuste no encontrado: " + request.motivoId()));

        if (!producto.isControlStock())
            throw new IllegalStateException("El producto '" + producto.getNombre() + "' no tiene control de stock activo.");

        BigDecimal cantidad = request.cantidad();
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");

        // Regla: si el motivo es DECREMENTO, la cantidad a guardar en stock debe ser negativa
        BigDecimal delta = motivo.getTipo().equals("DECREMENTO") ? cantidad.negate() : cantidad;

        // Registrar movimiento AJUSTE
        LocalDateTime ahora = LocalDateTime.now();
        stockMovementRepository.save(
                producto.getId(),
                "AJUSTE",
                delta,
                null, // No afecta precio costo promedio/referencia en ajustes simples
                null, // referencia_id
                motivo.getId(),
                request.nota(),
                ahora
        );

        // Actualizar stock físico
        if (delta.compareTo(BigDecimal.ZERO) < 0) {
            productoRepository.decrementarStock(producto.getId(), delta.abs());
        } else {
            // Nota: Aquí pasamos 0 como precio costo porque un ajuste positivo usualmente no debe promediar costo
            // a menos que sea una entrada de compra real (que es el otro UseCase).
            productoRepository.incrementarStockYActualizarCosto(producto.getId(), delta, producto.getPrecioCosto());
        }

        return new StockMovementDTO(null, producto.getId(), producto.getNombre(), "AJUSTE", delta, null, null, motivo.getNombre(), motivo.getTipo(), request.nota(), ahora);
    }
}
