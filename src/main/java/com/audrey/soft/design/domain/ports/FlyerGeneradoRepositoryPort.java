package com.audrey.soft.design.domain.ports;

import com.audrey.soft.design.infrastructure.persistence.entities.FlyerGeneradoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlyerGeneradoRepositoryPort {
    FlyerGeneradoEntity save(FlyerGeneradoEntity entity);
    Optional<FlyerGeneradoEntity> findByIdAndEmpresaId(UUID id, UUID empresaId);
    Page<FlyerGeneradoEntity> findByEmpresaId(UUID empresaId, Pageable pageable);
    List<String> findProductoIdsByEmpresaId(UUID empresaId);
}
