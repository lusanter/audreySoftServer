package com.audrey.soft.design.infrastructure.persistence.adapters;

import com.audrey.soft.design.application.dtos.PlantillaRequestDTO;
import com.audrey.soft.design.domain.models.Plantilla;
import com.audrey.soft.design.domain.ports.PlantillaRepositoryPort;
import com.audrey.soft.design.infrastructure.persistence.entities.PlantillaEntity;
import com.audrey.soft.design.infrastructure.persistence.repositories.SpringDataPlantillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlantillaRepositoryAdapter implements PlantillaRepositoryPort {

    private final SpringDataPlantillaRepository springDataRepository;

    @Override
    public Plantilla save(Plantilla plantilla) {
        PlantillaEntity entity = toEntity(plantilla);
        PlantillaEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Plantilla> findById(String id) {
        return springDataRepository.findById(UUID.fromString(id))
                .map(this::toDomain);
    }

    @Override
    public Optional<Plantilla> findByNombre(String nombre) {
        return springDataRepository.findByNombre(nombre)
                .map(this::toDomain);
    }

    @Override
    public List<Plantilla> findAllActive() {
        return springDataRepository.findByActivaTrueOrderByFechaCreacionDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Plantilla> findByTipoUso(PlantillaRequestDTO.TipoUso tipoUso) {
        return springDataRepository.findByTipoUsoAndActivaTrueOrderByFechaCreacionDesc(tipoUso)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Plantilla> findByFormato(PlantillaRequestDTO.Formato formato) {
        return springDataRepository.findByFormatoAndActivaTrueOrderByFechaCreacionDesc(formato)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Plantilla> findByOrigen(PlantillaRequestDTO.Origen origen) {
        return springDataRepository.findByOrigenAndActivaTrueOrderByFechaCreacionDesc(origen)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Plantilla> findByEmpresaId(String empresaId) {
        return springDataRepository.findByEmpresaIdAndActivaTrueOrderByFechaCreacionDesc(UUID.fromString(empresaId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Plantilla> findByTagsContaining(List<String> tags) {
        return springDataRepository.findByTagsContaining(tags)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Plantilla update(Plantilla plantilla) {
        PlantillaEntity entity = toEntity(plantilla);
        PlantillaEntity updatedEntity = springDataRepository.save(entity);
        return toDomain(updatedEntity);
    }

    @Override
    public void delete(String id) {
        springDataRepository.softDelete(UUID.fromString(id));
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return springDataRepository.existsByNombre(nombre);
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private PlantillaEntity toEntity(Plantilla plantilla) {
        return new PlantillaEntity(
                UUID.fromString(plantilla.getId()),
                plantilla.getNombre(),
                plantilla.getDescripcion(),
                plantilla.getTipoUso(),
                plantilla.getFormato(),
                plantilla.getTags(),
                plantilla.getOrigen(),
                plantilla.getCapas(),
                plantilla.getFechaCreacion(),
                plantilla.getActiva(),
                plantilla.getEmpresaId() != null ? UUID.fromString(plantilla.getEmpresaId()) : null
        );
    }

    private Plantilla toDomain(PlantillaEntity entity) {
        return new Plantilla(
                entity.getId().toString(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getTipoUso(),
                entity.getFormato(),
                entity.getTags(),
                entity.getOrigen(),
                entity.getCapas(),
                entity.getFechaCreacion(),
                entity.getActiva(),
                entity.getEmpresaId() != null ? entity.getEmpresaId().toString() : null
        );
    }
}