package com.audrey.soft.inventory.infrastructure.persistence.mappers;

import com.audrey.soft.inventory.domain.models.Categoria;
import com.audrey.soft.inventory.infrastructure.persistence.entities.CategoriaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriaEntityMapper {
    @Mapping(target = "sucursalId", source = "sucursal.id")
    Categoria toDomain(CategoriaEntity entity);

    @Mapping(target = "sucursal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CategoriaEntity toEntity(Categoria domain);
}
