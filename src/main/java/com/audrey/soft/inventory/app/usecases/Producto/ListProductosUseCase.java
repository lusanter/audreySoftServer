package com.audrey.soft.inventory.app.usecases.Producto;

import com.audrey.soft.inventory.app.dtos.ProductoDTO;
import com.audrey.soft.inventory.app.mappers.ProductoMapper;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListProductosUseCase {
    private final ProductoRepositoryPort productoRepository;
    private final ProductoMapper productoMapper;

    public ListProductosUseCase(ProductoRepositoryPort productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    public List<ProductoDTO> execute(UUID sucursalId) {
        return productoRepository.findBySucursalId(sucursalId)
                .stream().map(productoMapper::toDto).toList();
    }
}
