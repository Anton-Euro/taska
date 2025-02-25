package com.geml.taska.controllers;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  /**
   * constructor.
   */
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  /**
   * Получить всех пользователей.
   *
   * @return список DisplayUserDto
   */
  @GetMapping
  public ResponseEntity<List<DisplayUserDto>> getAllUsers() {
    List<DisplayUserDto> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Получить пользователя по id.
   *
   * @param id идентификатор пользователя
   */
  @GetMapping("/{id}")
  public ResponseEntity<DisplayUserDto> getUser(final @PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  /**
   * Создать нового пользователя.
   *
   * @param createUserDto объект CreateUserDto
   */
  @PostMapping
  public ResponseEntity<DisplayUserDto> createUser(final @RequestBody CreateUserDto createUserDto) {
    DisplayUserDto createdUser = userService.createUser(createUserDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  /**
   * Обновить пользователя.
   *
   * @param id идентификатор пользователя
   * @param createUserDto объект CreateUserDto с обновленными данными
   */
  @PutMapping("/{id}")
  public ResponseEntity<DisplayUserDto> updateUser(final @PathVariable Long id,
      final @RequestBody CreateUserDto createUserDto) {
    DisplayUserDto updatedUser = userService.updateUser(id, createUserDto);
    return ResponseEntity.ok(updatedUser);
  }

  /**
   * Удалить пользователя.
   *
   * @param id идентификатор пользователя
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(final @PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
