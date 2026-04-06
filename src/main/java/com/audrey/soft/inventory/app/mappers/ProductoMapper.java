package com.audrey.soft.inventory.app.mappers;

import com.audrey.soft.inventory.app.dtos.ProductoDTO;
import com.audrey.soft.inventory.domain.models.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductoMapper {
    ProductoDTO toDto(Producto producto);
    Producto toDomain(ProductoDTO dto);
}
