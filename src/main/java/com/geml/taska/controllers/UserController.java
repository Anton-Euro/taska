package com.geml.taska.controllers;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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


@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;


    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayUserDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<DisplayUserDto>> getAllUsers() {
        List<DisplayUserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно получен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayUserDto.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayUserDto> getUser(
        @Parameter(description = "Идентификатор пользователя", example = "1") final @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayUserDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DisplayUserDto> createUser(
        @Parameter(description = "Данные для создания пользователя") final @Valid @RequestBody CreateUserDto createUserDto
    ) {
        DisplayUserDto createdUser = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет существующего пользователя.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayUserDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayUserDto> updateUser(
        @Parameter(description = "Идентификатор пользователя для обновления", example = "1") final @PathVariable Long id,
        @Parameter(description = "Данные для обновления пользователя") final @Valid @RequestBody CreateUserDto createUserDto
    ) {
        DisplayUserDto updatedUser = userService.updateUser(id, createUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Пользователь успешно удален", content = @Content),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "Идентификатор пользователя для удаления", example = "1") final @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
