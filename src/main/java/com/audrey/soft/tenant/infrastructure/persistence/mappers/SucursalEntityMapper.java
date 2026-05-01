package com.audrey.soft.tenant.infrastructure.persistence.mappers;

import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SucursalEntityMapper {

    @Mapping(target = "empresaId", source = "empresa.id")
    @Mapping(target = "paisCodigo", source = "paisCodigo")
    @Mapping(target = "monedaCodigo", source = "monedaCodigo")
    Sucursal toDomain(SucursalEntity entity);

    // toEntity se hace manualmente en el adaptador porque necesita cargar EmpresaEntity
}
