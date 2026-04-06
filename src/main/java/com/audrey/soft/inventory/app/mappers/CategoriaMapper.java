package com.audrey.soft.inventory.app.mappers;

import com.audrey.soft.inventory.app.dtos.CategoriaDTO;
import com.audrey.soft.inventory.domain.models.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriaMapper {
    CategoriaDTO toDto(Categoria categoria);
    Categoria toDomain(CategoriaDTO dto);
}
