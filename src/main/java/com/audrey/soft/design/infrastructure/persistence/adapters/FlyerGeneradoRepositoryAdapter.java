package com.audrey.soft.design.infrastructure.persistence.adapters;

import com.audrey.soft.design.domain.ports.FlyerGeneradoRepositoryPort;
import com.audrey.soft.design.infrastructure.persistence.entities.FlyerGeneradoEntity;
import com.audrey.soft.design.infrastructure.persistence.repositories.SpringDataFlyerGeneradoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlyerGeneradoRepositoryAdapter implements FlyerGeneradoRepositoryPort {

    private final SpringDataFlyerGeneradoRepository repository;

    @Override
    public FlyerGeneradoEntity save(FlyerGeneradoEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<FlyerGeneradoEntity> findByIdAndEmpresaId(UUID id, UUID empresaId) {
        return repository.findByIdAndEmpresaId(id, empresaId);
    }

    @Override
    public Page<FlyerGeneradoEntity> findByEmpresaId(UUID empresaId, Pageable pageable) {
        return repository.findByEmpresaIdOrderByCreatedAtDesc(empresaId, pageable);
    }

    @Override
    public List<String> findProductoIdsByEmpresaId(UUID empresaId) {
        return repository.findProductoIdsByEmpresaId(empresaId);
    }
}
