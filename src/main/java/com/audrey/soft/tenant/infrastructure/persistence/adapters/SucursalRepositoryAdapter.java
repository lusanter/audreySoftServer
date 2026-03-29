package com.audrey.soft.tenant.infrastructure.persistence.adapters;

import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;
import com.audrey.soft.tenant.infrastructure.persistence.entities.EmpresaEntity;
import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import com.audrey.soft.tenant.infrastructure.persistence.mappers.SucursalEntityMapper;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataEmpresaRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SucursalRepositoryAdapter implements SucursalRepositoryPort {

    private final SpringDataSucursalRepository sucursalJpa;
    private final SpringDataEmpresaRepository empresaJpa;
    private final SucursalEntityMapper mapper;

    public SucursalRepositoryAdapter(SpringDataSucursalRepository sucursalJpa,
                                     SpringDataEmpresaRepository empresaJpa,
                                     SucursalEntityMapper mapper) {
        this.sucursalJpa = sucursalJpa;
        this.empresaJpa = empresaJpa;
        this.mapper = mapper;
    }

    @Override
    public Sucursal save(Sucursal sucursal) {
        EmpresaEntity empresa = empresaJpa.findById(sucursal.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Empresa no encontrada: " + sucursal.getEmpresaId()));

        SucursalEntity entity = SucursalEntity.builder()
                .id(sucursal.getId())
                .empresa(empresa)
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .active(sucursal.isActive())
                .build();

        return mapper.toDomain(sucursalJpa.save(entity));
    }

    @Override
    public Optional<Sucursal> findById(UUID id) {
        return sucursalJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Sucursal> findByEmpresaId(UUID empresaId) {
        return sucursalJpa.findByEmpresaId(empresaId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNombreAndEmpresaId(String nombre, UUID empresaId) {
        return sucursalJpa.existsByNombreAndEmpresaId(nombre, empresaId);
    }
}
