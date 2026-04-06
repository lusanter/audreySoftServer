package com.audrey.soft.inventory.app.usecases.Cliente;

import com.audrey.soft.inventory.app.dtos.ClienteDTO;
import com.audrey.soft.inventory.app.mappers.ClienteMapper;
import com.audrey.soft.inventory.domain.ports.ClienteRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListClientesUseCase {
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;

    public ListClientesUseCase(ClienteRepositoryPort clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteDTO> execute(UUID sucursalId) {
        return clienteRepository.findBySucursalId(sucursalId)
                .stream().map(clienteMapper::toDto).toList();
    }
}
