package com.audrey.soft.inventory.app.usecases.Categoria;

import com.audrey.soft.inventory.app.dtos.CategoriaDTO;
import com.audrey.soft.inventory.app.mappers.CategoriaMapper;
import com.audrey.soft.inventory.domain.ports.CategoriaRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListCategoriasUseCase {
    private final CategoriaRepositoryPort categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public ListCategoriasUseCase(CategoriaRepositoryPort categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public List<CategoriaDTO> execute(UUID sucursalId) {
        return categoriaRepository.findBySucursalId(sucursalId)
                .stream().map(categoriaMapper::toDto).toList();
    }
}
