package com.audrey.soft.design.infrastructure.persistence.repositories;

import com.audrey.soft.design.infrastructure.persistence.entities.FlyerGeneradoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFlyerGeneradoRepository extends JpaRepository<FlyerGeneradoEntity, UUID> {

    Optional<FlyerGeneradoEntity> findByIdAndEmpresaId(UUID id, UUID empresaId);

    Page<FlyerGeneradoEntity> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId, Pageable pageable);

    @Query(value = """
            SELECT jsonb_array_elements_text(producto_ids)
            FROM design.flyers_generados
            WHERE empresa_id = :empresaId
            """, nativeQuery = true)
    List<String> findProductoIdsByEmpresaId(@Param("empresaId") UUID empresaId);
}
