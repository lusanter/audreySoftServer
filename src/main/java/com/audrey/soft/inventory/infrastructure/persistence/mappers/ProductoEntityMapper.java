package com.audrey.soft.inventory.infrastructure.persistence.mappers;

import com.audrey.soft.inventory.domain.models.Producto;
import com.audrey.soft.inventory.infrastructure.persistence.entities.ProductoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductoEntityMapper {
    @Mapping(target = "sucursalId", source = "sucursal.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    Producto toDomain(ProductoEntity entity);

    @Mapping(target = "sucursal", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductoEntity toEntity(Producto domain);
}
