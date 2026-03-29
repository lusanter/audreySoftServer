package com.audrey.soft.tenant.app.mappers;

import com.audrey.soft.tenant.app.dtos.EmpresaDTO;
import com.audrey.soft.tenant.domain.models.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmpresaMapper {
    EmpresaDTO toDto(Empresa empresa);
    Empresa toDomain(EmpresaDTO dto);
}
