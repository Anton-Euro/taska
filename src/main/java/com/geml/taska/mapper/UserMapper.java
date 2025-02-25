package com.geml.taska.mapper;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.models.User;
import org.springframework.stereotype.Component;

/**
 * Mapper для преобразования между CreateUserDto, DisplayUserDto и User.
 */
@Component
public final class UserMapper {

  private UserMapper() {
  }

  /**
   * Преобразует CreateUserDto в сущность User.
   *
   * @param createDto объект CreateUserDto
   * @return сущность User
   */
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

  /**
   * Преобразует сущность User в DisplayUserDto.
   *
   * @param user сущность User
   * @return объект DisplayUserDto
   */
  public DisplayUserDto toDisplayUserDto(final User user) {
    if (user == null) {
      return null;
    }
    return new DisplayUserDto(user.getId(), user.getUsername(), user.getEmail());
  }
}
