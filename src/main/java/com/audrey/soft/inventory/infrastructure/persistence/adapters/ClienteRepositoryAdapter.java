package com.audrey.soft.inventory.infrastructure.persistence.adapters;

import com.audrey.soft.inventory.domain.models.Cliente;
import com.audrey.soft.inventory.domain.ports.ClienteRepositoryPort;
import com.audrey.soft.inventory.infrastructure.persistence.entities.ClienteEntity;
import com.audrey.soft.inventory.infrastructure.persistence.mappers.ClienteEntityMapper;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataClienteRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;
    private final ClienteEntityMapper mapper;

    public ClienteRepositoryAdapter(SpringDataClienteRepository jpa,
                                    SpringDataSucursalRepository sucursalJpa,
                                    ClienteEntityMapper mapper) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
        this.mapper = mapper;
    }

    @Override
    public Cliente save(Cliente cliente) {
        var sucursal = sucursalJpa.findById(cliente.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + cliente.getSucursalId()));
        ClienteEntity entity = ClienteEntity.builder()
                .id(cliente.getId())
                .sucursal(sucursal)
                .nombre(cliente.getNombre())
                .documento(cliente.getDocumento())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .active(cliente.isActive())
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByDocumentoAndSucursalId(String documento, UUID sucursalId) {
        return jpa.existsByDocumentoAndSucursalId(documento, sucursalId);
    }

    @Override
    public List<Cliente> buscar(UUID sucursalId, String nombre, String documento, boolean soloActivos) {
        String nombrePattern   = (nombre    != null && !nombre.isBlank())    ? "%" + nombre    + "%" : "%";
        String documentoPattern = (documento != null && !documento.isBlank()) ? "%" + documento + "%" : "%";
        return jpa.buscar(sucursalId, nombrePattern, documentoPattern, soloActivos)
                .stream().map(mapper::toDomain).toList();
    }
}
