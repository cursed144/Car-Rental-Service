package com.example.carrental.mapper;

import com.example.carrental.dto.UserResponseDto;
import com.example.carrental.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleName", expression = "java(user.getRole().getName().name())")
    UserResponseDto toDto(User user);
}
