package com.audrey.soft.inventory.app.usecases.Categoria;

import com.audrey.soft.inventory.app.dtos.CategoriaDTO;
import com.audrey.soft.inventory.app.mappers.CategoriaMapper;
import com.audrey.soft.inventory.domain.ports.CategoriaRepositoryPort;

import java.util.UUID;

public class UpdateCategoriaUseCase {
    private final CategoriaRepositoryPort categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public UpdateCategoriaUseCase(CategoriaRepositoryPort categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaDTO execute(UUID id, CategoriaDTO request) {
        var categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + id));
        categoria.setNombre(request.nombre());
        categoria.setActive(request.active());
        return categoriaMapper.toDto(categoriaRepository.save(categoria));
    }
}
