package com.audrey.soft.design.domain.ports;

import com.audrey.soft.design.domain.models.Plantilla;
import com.audrey.soft.design.application.dtos.PlantillaRequestDTO;

import java.util.List;
import java.util.Optional;

public interface PlantillaRepositoryPort {
    
    /**
     * Guarda una nueva plantilla
     */
    Plantilla save(Plantilla plantilla);
    
    /**
     * Busca una plantilla por ID
     */
    Optional<Plantilla> findById(String id);
    
    /**
     * Busca plantillas por nombre (para validar duplicados)
     */
    Optional<Plantilla> findByNombre(String nombre);
    
    /**
     * Obtiene todas las plantillas activas
     */
    List<Plantilla> findAllActive();
    
    /**
     * Obtiene plantillas por tipo de uso
     */
    List<Plantilla> findByTipoUso(PlantillaRequestDTO.TipoUso tipoUso);
    
    /**
     * Obtiene plantillas por formato
     */
    List<Plantilla> findByFormato(PlantillaRequestDTO.Formato formato);
    
    /**
     * Obtiene plantillas por origen (SUPER_ADMIN o EMPRESA)
     */
    List<Plantilla> findByOrigen(PlantillaRequestDTO.Origen origen);
    
    /**
     * Obtiene plantillas de una empresa específica
     */
    List<Plantilla> findByEmpresaId(String empresaId);
    
    /**
     * Busca plantillas por tags
     */
    List<Plantilla> findByTagsContaining(List<String> tags);
    
    /**
     * Actualiza una plantilla existente
     */
    Plantilla update(Plantilla plantilla);
    
    /**
     * Elimina una plantilla (soft delete - marca como inactiva)
     */
    void delete(String id);
    
    /**
     * Verifica si existe una plantilla con el mismo nombre
     */
    boolean existsByNombre(String nombre);
}