package com.audrey.soft.restaurant.app.usecases.Comanda;

import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.restaurant.app.dtos.AgregarItemRequest;
import com.audrey.soft.restaurant.app.dtos.ComandaDTO;
import com.audrey.soft.restaurant.app.mappers.ComandaMapper;
import com.audrey.soft.restaurant.domain.models.ComandaItem;
import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.restaurant.domain.models.EstadoItem;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;

import java.math.BigDecimal;
import java.util.UUID;

public class AgregarItemUseCase {
    private final ComandaRepositoryPort comandaRepository;
    private final ProductoRepositoryPort productoRepository;
    private final ComandaMapper comandaMapper;

    public AgregarItemUseCase(ComandaRepositoryPort comandaRepository,
                              ProductoRepositoryPort productoRepository,
                              ComandaMapper comandaMapper) {
        this.comandaRepository = comandaRepository;
        this.productoRepository = productoRepository;
        this.comandaMapper = comandaMapper;
    }

    public ComandaDTO execute(UUID comandaId, AgregarItemRequest request) {
        var comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("Comanda no encontrada: " + comandaId));

        if (comanda.getEstado() == EstadoComanda.CERRADA || comanda.getEstado() == EstadoComanda.CANCELADA)
            throw new IllegalStateException("No se puede agregar items a una comanda " + comanda.getEstado());

        var producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + request.productoId()));

        ComandaItem item = new ComandaItem(null, comandaId, request.productoId(),
                request.cantidad(), producto.getPrecio(), request.notas(), null, EstadoItem.PENDIENTE, null, null);

        comanda.getItems().add(item);

        // Recalcular total
        BigDecimal nuevoTotal = comanda.getItems().stream()
                .filter(i -> i.getEstado() != EstadoItem.CANCELADO)
                .map(i -> i.getPrecioUnitario().multiply(i.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        comanda.setTotal(nuevoTotal);
        comanda.setEstado(EstadoComanda.EN_COCINA);

        return comandaMapper.toDto(comandaRepository.save(comanda));
    }
}
