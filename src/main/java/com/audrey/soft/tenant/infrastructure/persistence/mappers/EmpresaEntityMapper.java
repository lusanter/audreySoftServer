package com.audrey.soft.tenant.infrastructure.persistence.mappers;

import com.audrey.soft.tenant.domain.models.Empresa;
import com.audrey.soft.tenant.infrastructure.persistence.entities.EmpresaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmpresaEntityMapper {
    Empresa toDomain(EmpresaEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EmpresaEntity toEntity(Empresa domain);
}
