package com.audrey.soft.tenant.infrastructure.persistence.adapters;

import com.audrey.soft.tenant.domain.models.Empresa;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;
import com.audrey.soft.tenant.infrastructure.persistence.mappers.EmpresaEntityMapper;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataEmpresaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmpresaRepositoryAdapter implements EmpresaRepositoryPort {

    private final SpringDataEmpresaRepository jpaRepository;
    private final EmpresaEntityMapper mapper;

    public EmpresaRepositoryAdapter(SpringDataEmpresaRepository jpaRepository,
                                    EmpresaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Empresa save(Empresa empresa) {
        var entity = mapper.toEntity(empresa);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Empresa> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Empresa> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByRuc(String ruc) {
        return jpaRepository.existsByRuc(ruc);
    }
}
