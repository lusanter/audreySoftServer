package com.audrey.soft.inventory.app.usecases.Cliente;

import com.audrey.soft.inventory.app.dtos.ClienteDTO;
import com.audrey.soft.inventory.app.mappers.ClienteMapper;
import com.audrey.soft.inventory.domain.ports.ClienteRepositoryPort;

import java.util.UUID;

public class UpdateClienteUseCase {
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;

    public UpdateClienteUseCase(ClienteRepositoryPort clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteDTO execute(UUID clienteId, ClienteDTO request) {
        var cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + clienteId));

        cliente.setNombre(request.nombre());
        cliente.setDocumento(request.documento());
        cliente.setEmail(request.email());
        cliente.setTelefono(request.telefono());
        cliente.setActive(request.active());

        return clienteMapper.toDto(clienteRepository.save(cliente));
    }
}
