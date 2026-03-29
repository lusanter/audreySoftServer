package com.audrey.soft.tenant.app.mappers;

import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.domain.models.Sucursal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SucursalMapper {
    SucursalDTO toDto(Sucursal sucursal);
    Sucursal toDomain(SucursalDTO dto);
}
