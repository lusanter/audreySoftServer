package com.audrey.soft.inventory.app.usecases.Cliente;

import com.audrey.soft.inventory.app.dtos.ClienteDTO;
import com.audrey.soft.inventory.app.mappers.ClienteMapper;
import com.audrey.soft.inventory.domain.ports.ClienteRepositoryPort;

import java.util.List;
import java.util.UUID;

public class BuscarClientesUseCase {
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;

    public BuscarClientesUseCase(ClienteRepositoryPort clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteDTO> execute(UUID sucursalId, String nombre, String documento, boolean soloActivos) {
        return clienteRepository.buscar(sucursalId, nombre, documento, soloActivos)
                .stream().map(clienteMapper::toDto).toList();
    }
}
