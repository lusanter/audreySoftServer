package com.audrey.soft.inventory.app.usecases.Categoria;

import com.audrey.soft.inventory.app.dtos.CategoriaDTO;
import com.audrey.soft.inventory.app.mappers.CategoriaMapper;
import com.audrey.soft.inventory.domain.models.Categoria;
import com.audrey.soft.inventory.domain.ports.CategoriaRepositoryPort;

import java.util.UUID;

public class CreateCategoriaUseCase {
    private final CategoriaRepositoryPort categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CreateCategoriaUseCase(CategoriaRepositoryPort categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaDTO execute(UUID sucursalId, CategoriaDTO request) {
        if (categoriaRepository.existsByNombreAndSucursalId(request.nombre(), sucursalId))
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre en esta sucursal");
        Categoria nueva = new Categoria(null, sucursalId, request.nombre(), true, null, null);
        return categoriaMapper.toDto(categoriaRepository.save(nueva));
    }
}
