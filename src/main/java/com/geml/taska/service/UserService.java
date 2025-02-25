package com.geml.taska.service;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.mapper.UserMapper;
import com.geml.taska.models.User;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


/**
 * Сервис для управления пользователями.
 */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * Costructor.
   */
  public UserService(final UserRepository userRepository, final UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  /**
   * Получить всех пользователей.
   *
   * @return список DisplayUserDto
   */
  public List<DisplayUserDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(userMapper::toDisplayUserDto)
        .collect(Collectors.toList());
  }

  /**
   * Получить пользователя по идентификатору.
   *
   * @param id идентификатор пользователя
   * @return DisplayUserDto
   */
  public DisplayUserDto getUserById(final Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    return userMapper.toDisplayUserDto(user);
  }

  /**
   * Создать пользователя.
   *
   * @param userDto объект CreateUserDto
   * @return созданный DisplayUserDto
   */
  public DisplayUserDto createUser(final CreateUserDto userDto) {
    User user = userMapper.fromCreateUserDto(userDto);
    User saved = userRepository.save(user);
    return userMapper.toDisplayUserDto(saved);
  }

  /**
   * Обновить пользователя.
   *
   * @param id идентификатор пользователя
   * @param userDto обновленные данные
   * @return обновленный DisplayUserDto
   */
  public DisplayUserDto updateUser(final Long id, final CreateUserDto userDto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    user.setPassword(userDto.getPassword());
    
    User saved = userRepository.save(user);
    return userMapper.toDisplayUserDto(saved);
  }

  /**
   * Удалить пользователя.
   *
   * @param id идентификатор пользователя
   */
  public void deleteUser(final Long id) {
    if (!userRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    userRepository.deleteById(id);
  }
}