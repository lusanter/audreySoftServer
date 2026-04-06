package com.audrey.soft.restaurant.app.usecases.Comanda;

import com.audrey.soft.restaurant.app.dtos.ComandaDTO;
import com.audrey.soft.restaurant.app.mappers.ComandaMapper;
import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;

import java.util.UUID;

public class MoverItemSubcuentaUseCase {
    private final ComandaRepositoryPort comandaRepository;
    private final ComandaMapper comandaMapper;

    public MoverItemSubcuentaUseCase(ComandaRepositoryPort comandaRepository, ComandaMapper comandaMapper) {
        this.comandaRepository = comandaRepository;
        this.comandaMapper = comandaMapper;
    }

    public ComandaDTO execute(UUID comandaId, UUID itemId, String subCuenta) {
        var comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("Comanda no encontrada: " + comandaId));

        if (comanda.getEstado() == EstadoComanda.CERRADA || comanda.getEstado() == EstadoComanda.CANCELADA)
            throw new IllegalStateException("No se puede modificar una comanda " + comanda.getEstado());

        comanda.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId))
                .setSubCuenta(subCuenta);

        return comandaMapper.toDto(comandaRepository.save(comanda));
    }
}
