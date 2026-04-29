package com.audrey.soft.inventory.app.usecases.Producto;

import com.audrey.soft.inventory.app.dtos.ProductoDTO;
import com.audrey.soft.inventory.app.mappers.ProductoMapper;
import com.audrey.soft.inventory.domain.models.Producto;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateProductoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final ProductoMapper productoMapper;
    private final StockMovementRepositoryPort stockMovementRepository;

    public CreateProductoUseCase(ProductoRepositoryPort productoRepository,
                                 ProductoMapper productoMapper,
                                 StockMovementRepositoryPort stockMovementRepository) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public ProductoDTO execute(UUID sucursalId, ProductoDTO request) {
        BigDecimal stockInicial = request.stockActual() != null ? request.stockActual() : BigDecimal.ZERO;
        BigDecimal precioCosto  = request.precioCosto() != null ? request.precioCosto() : BigDecimal.ZERO;
        boolean controlStock    = request.controlStock() != null && request.controlStock();

        Producto nuevo = new Producto(
                null, sucursalId, request.categoriaId(),
                request.nombre(), request.descripcion(),
                request.precio() != null ? request.precio() : BigDecimal.ZERO,
                stockInicial,
                request.stockMinimo() != null ? request.stockMinimo() : BigDecimal.ZERO,
                request.unidad() != null ? request.unidad() : "UND",
                true, controlStock, precioCosto,
                request.imagenUrl(),
                null, null
        );

        Producto guardado = productoRepository.save(nuevo);

        // Si el producto controla stock y tiene stock inicial > 0, registrar movimiento ENTRADA inicial
        if (controlStock && stockInicial.compareTo(BigDecimal.ZERO) > 0) {
            stockMovementRepository.save(
                    guardado.getId(),
                    "ENTRADA",
                    stockInicial,
                    precioCosto,
                    null,
                    null,                // motivoId
                    "Ingreso inicial al inventario",
                    LocalDateTime.now()
            );
        }

        return productoMapper.toDto(guardado);
    }
}
