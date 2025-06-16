package com.taskmanager.user.application;

import com.taskmanager.user.application.dto.UserDTO;
import com.taskmanager.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "fullName", source = "fullName")
    UserDTO toDTO(User user);
}