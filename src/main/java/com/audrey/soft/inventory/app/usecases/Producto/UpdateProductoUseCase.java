package com.audrey.soft.inventory.app.usecases.Producto;

import com.audrey.soft.inventory.app.dtos.ProductoDTO;
import com.audrey.soft.inventory.app.mappers.ProductoMapper;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public class UpdateProductoUseCase {
    private final ProductoRepositoryPort productoRepository;
    private final ProductoMapper productoMapper;

    public UpdateProductoUseCase(ProductoRepositoryPort productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    public ProductoDTO execute(UUID productoId, ProductoDTO request) {
        var producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        if (request.precioCosto() == null || request.precioCosto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Precio Costo debería ser mayor a 0");
        } else {
            producto.setPrecioCosto(request.precioCosto());
        }
        producto.setCategoriaId(request.categoriaId());
        // stockActual NO se actualiza por edición directa — solo via movimientos
        producto.setStockMinimo(request.stockMinimo());
        producto.setUnidad(request.unidad());
        producto.setActive(request.active());
        producto.setControlStock(request.controlStock());
        producto.setImagenUrl(request.imagenUrl());
        return productoMapper.toDto(productoRepository.save(producto));
    }
}
