package com.audrey.soft.fiscal.infrastructure.persistence.repositories;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.ImpuestoTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataImpuestoTipoRepository extends JpaRepository<ImpuestoTipoEntity, String> {

    List<ImpuestoTipoEntity> findAllByIdIn(List<String> ids);
}
