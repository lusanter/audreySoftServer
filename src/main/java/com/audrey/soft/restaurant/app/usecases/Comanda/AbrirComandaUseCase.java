package com.audrey.soft.restaurant.app.usecases.Comanda;

import com.audrey.soft.restaurant.app.dtos.ComandaDTO;
import com.audrey.soft.restaurant.app.mappers.ComandaMapper;
import com.audrey.soft.restaurant.domain.models.Comanda;
import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class AbrirComandaUseCase {
    private final ComandaRepositoryPort comandaRepository;
    private final MesaRepositoryPort mesaRepository;
    private final ComandaMapper comandaMapper;

    public AbrirComandaUseCase(ComandaRepositoryPort comandaRepository,
                               MesaRepositoryPort mesaRepository,
                               ComandaMapper comandaMapper) {
        this.comandaRepository = comandaRepository;
        this.mesaRepository = mesaRepository;
        this.comandaMapper = comandaMapper;
    }

    public ComandaDTO execute(UUID sucursalId, UUID mesaId, UUID clienteId, String notas) {
        // Validar que la mesa no tenga comanda abierta
        comandaRepository.findOpenByMesaId(mesaId).ifPresent(c -> {
            throw new IllegalStateException("La mesa ya tiene una comanda abierta: " + c.getId());
        });

        Comanda nueva = new Comanda(null, sucursalId, mesaId, clienteId,
                EstadoComanda.ABIERTA, BigDecimal.ZERO, notas, new ArrayList<>(), null, null);
        Comanda guardada = comandaRepository.save(nueva);

        // Marcar mesa como OCUPADA
        mesaRepository.updateEstado(mesaId, EstadoMesa.OCUPADA);

        return comandaMapper.toDto(guardada);
    }
}
