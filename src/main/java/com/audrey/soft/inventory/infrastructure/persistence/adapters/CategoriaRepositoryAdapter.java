package com.audrey.soft.inventory.infrastructure.persistence.adapters;

import com.audrey.soft.inventory.domain.models.Categoria;
import com.audrey.soft.inventory.domain.ports.CategoriaRepositoryPort;
import com.audrey.soft.inventory.infrastructure.persistence.entities.CategoriaEntity;
import com.audrey.soft.inventory.infrastructure.persistence.mappers.CategoriaEntityMapper;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataCategoriaRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final SpringDataCategoriaRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;
    private final CategoriaEntityMapper mapper;

    public CategoriaRepositoryAdapter(SpringDataCategoriaRepository jpa,
                                      SpringDataSucursalRepository sucursalJpa,
                                      CategoriaEntityMapper mapper) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
        this.mapper = mapper;
    }

    @Override
    public Categoria save(Categoria categoria) {
        var sucursal = sucursalJpa.findById(categoria.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + categoria.getSucursalId()));
        CategoriaEntity entity = CategoriaEntity.builder()
                .id(categoria.getId())
                .sucursal(sucursal)
                .nombre(categoria.getNombre())
                .active(categoria.isActive())
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Categoria> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Categoria> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNombreAndSucursalId(String nombre, UUID sucursalId) {
        return jpa.existsByNombreAndSucursalId(nombre, sucursalId);
    }
}
