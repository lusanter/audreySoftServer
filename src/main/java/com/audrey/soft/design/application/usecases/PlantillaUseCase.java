package com.audrey.soft.design.application.usecases;

import com.audrey.soft.design.application.dtos.PlantillaRequestDTO;
import com.audrey.soft.design.application.dtos.PlantillaResponseDTO;
import com.audrey.soft.design.domain.models.Plantilla;
import com.audrey.soft.design.domain.ports.PlantillaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlantillaUseCase {

    private final PlantillaRepositoryPort plantillaRepository;

    /**
     * Crea una nueva plantilla
     */
    public PlantillaResponseDTO crearPlantilla(PlantillaRequestDTO request, String empresaId) {
        // Validar que no exista una plantilla con el mismo nombre
        if (plantillaRepository.existsByNombre(request.nombre())) {
            throw new IllegalArgumentException("Ya existe una plantilla con el nombre: " + request.nombre());
        }

        // Crear la plantilla
        Plantilla plantilla = new Plantilla(
                request.nombre(),
                request.descripcion(),
                request.tipoUso(),
                request.formato(),
                request.tags(),
                request.origen(),
                request.capas(),
                empresaId
        );

        // Guardar en el repositorio
        Plantilla plantillaGuardada = plantillaRepository.save(plantilla);

        // Convertir a DTO de respuesta
        return toResponseDTO(plantillaGuardada);
    }

    /**
     * Obtiene todas las plantillas activas
     */
    @Transactional(readOnly = true)
    public List<PlantillaResponseDTO> obtenerPlantillasActivas() {
        return plantillaRepository.findAllActive()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Obtiene una plantilla por ID
     */
    @Transactional(readOnly = true)
    public PlantillaResponseDTO obtenerPlantilla(String id) {
        Plantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada: " + id));
        
        return toResponseDTO(plantilla);
    }

    /**
     * Actualiza una plantilla existente
     */
    public PlantillaResponseDTO actualizarPlantilla(String id, PlantillaRequestDTO request) {
        Plantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada: " + id));

        // Validar que no exista otra plantilla con el mismo nombre
        plantillaRepository.findByNombre(request.nombre())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new IllegalArgumentException("Ya existe otra plantilla con el nombre: " + request.nombre());
                    }
                });

        // Actualizar la plantilla
        plantilla.actualizar(
                request.nombre(),
                request.descripcion(),
                request.tipoUso(),
                request.formato(),
                request.tags(),
                request.capas()
        );

        // Guardar cambios
        Plantilla plantillaActualizada = plantillaRepository.update(plantilla);

        return toResponseDTO(plantillaActualizada);
    }

    /**
     * Elimina una plantilla (soft delete)
     */
    public void eliminarPlantilla(String id) {
        Plantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada: " + id));

        plantillaRepository.delete(id);
    }

    /**
     * Obtiene plantillas por tipo de uso
     */
    @Transactional(readOnly = true)
    public List<PlantillaResponseDTO> obtenerPlantillasPorTipo(PlantillaRequestDTO.TipoUso tipoUso) {
        return plantillaRepository.findByTipoUso(tipoUso)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Obtiene plantillas por formato
     */
    @Transactional(readOnly = true)
    public List<PlantillaResponseDTO> obtenerPlantillasPorFormato(PlantillaRequestDTO.Formato formato) {
        return plantillaRepository.findByFormato(formato)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Convierte una entidad Plantilla a DTO de respuesta
     */
    private PlantillaResponseDTO toResponseDTO(Plantilla plantilla) {
        return new PlantillaResponseDTO(
                plantilla.getId(),
                plantilla.getNombre(),
                plantilla.getDescripcion(),
                plantilla.getTipoUso(),
                plantilla.getFormato(),
                plantilla.getTags(),
                plantilla.getOrigen(),
                plantilla.getCapas(),
                plantilla.getFechaCreacion(),
                plantilla.getActiva()
        );
    }
}