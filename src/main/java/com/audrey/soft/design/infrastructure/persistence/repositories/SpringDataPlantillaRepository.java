package com.audrey.soft.design.infrastructure.persistence.repositories;

import com.audrey.soft.design.application.dtos.PlantillaRequestDTO;
import com.audrey.soft.design.infrastructure.persistence.entities.PlantillaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPlantillaRepository extends JpaRepository<PlantillaEntity, UUID> {
    
    /**
     * Busca plantilla por nombre
     */
    Optional<PlantillaEntity> findByNombre(String nombre);
    
    /**
     * Verifica si existe una plantilla con el nombre dado
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtiene todas las plantillas activas
     */
    List<PlantillaEntity> findByActivaTrueOrderByFechaCreacionDesc();
    
    /**
     * Obtiene plantillas por tipo de uso
     */
    List<PlantillaEntity> findByTipoUsoAndActivaTrueOrderByFechaCreacionDesc(PlantillaRequestDTO.TipoUso tipoUso);
    
    /**
     * Obtiene plantillas por formato
     */
    List<PlantillaEntity> findByFormatoAndActivaTrueOrderByFechaCreacionDesc(PlantillaRequestDTO.Formato formato);
    
    /**
     * Obtiene plantillas por origen
     */
    List<PlantillaEntity> findByOrigenAndActivaTrueOrderByFechaCreacionDesc(PlantillaRequestDTO.Origen origen);
    
    /**
     * Obtiene plantillas de una empresa específica
     */
    List<PlantillaEntity> findByEmpresaIdAndActivaTrueOrderByFechaCreacionDesc(UUID empresaId);
    
    /**
     * Busca plantillas que contengan alguno de los tags especificados
     */
    @Query("SELECT p FROM PlantillaEntity p WHERE p.activa = true AND " +
           "EXISTS (SELECT 1 FROM p.tags t WHERE t IN :tags) " +
           "ORDER BY p.fechaCreacion DESC")
    List<PlantillaEntity> findByTagsContaining(@Param("tags") List<String> tags);
    
    /**
     * Soft delete - marca como inactiva
     */
    @Modifying
    @Query("UPDATE PlantillaEntity p SET p.activa = false WHERE p.id = :id")
    void softDelete(@Param("id") UUID id);
}