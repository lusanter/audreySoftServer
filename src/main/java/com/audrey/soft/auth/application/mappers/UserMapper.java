package com.audrey.soft.auth.application.mappers;

import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.domain.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDTO toDto(User user);
}