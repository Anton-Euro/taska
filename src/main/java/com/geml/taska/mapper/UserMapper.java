package com.geml.taska.mapper;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.models.User;
import org.springframework.stereotype.Component;


@Component
public final class UserMapper {

    private UserMapper() {
    }


    public User fromCreateUserDto(final CreateUserDto createDto) {
        if (createDto == null) {
            return null;
        }
        User user = new User();
        user.setUsername(createDto.getUsername());
        user.setEmail(createDto.getEmail());
        user.setPassword(createDto.getPassword());
        return user;
    }


    public DisplayUserDto toDisplayUserDto(final User user) {
        if (user == null) {
            return null;
        }
        return new DisplayUserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
