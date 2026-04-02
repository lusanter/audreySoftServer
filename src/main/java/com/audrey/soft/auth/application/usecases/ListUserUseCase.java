package com.audrey.soft.auth.application.usecases;

import com.audrey.soft.auth.application.dtos.UserDTO;
import com.audrey.soft.auth.application.mappers.UserMapper;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;

import java.util.List;

public class ListUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    public ListUserUseCase(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }

    public List<UserDTO> execute(){
        List<UserDTO> users = userRepositoryPort.getAll().stream().map(userMapper::toDto).toList();
        return users;
    }
}
