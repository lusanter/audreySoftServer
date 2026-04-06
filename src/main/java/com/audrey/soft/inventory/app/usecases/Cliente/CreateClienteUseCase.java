package com.audrey.soft.inventory.app.usecases.Cliente;

import com.audrey.soft.inventory.app.dtos.ClienteDTO;
import com.audrey.soft.inventory.app.mappers.ClienteMapper;
import com.audrey.soft.inventory.domain.models.Cliente;
import com.audrey.soft.inventory.domain.ports.ClienteRepositoryPort;

import java.util.UUID;

public class CreateClienteUseCase {
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;

    public CreateClienteUseCase(ClienteRepositoryPort clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteDTO execute(UUID sucursalId, ClienteDTO request) {
        if (request.documento() != null &&
                clienteRepository.existsByDocumentoAndSucursalId(request.documento(), sucursalId))
            throw new IllegalArgumentException("Ya existe un cliente con ese documento en esta sucursal");
        Cliente nuevo = new Cliente(null, sucursalId, request.nombre(), request.documento(),
                request.email(), request.telefono(), true, null, null);
        return clienteMapper.toDto(clienteRepository.save(nuevo));
    }
}
