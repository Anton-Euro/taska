package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.service.TaskService;
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
@RequestMapping("/api/tasks")
@Tag(name = "Task", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;


    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список задач успешно получен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTaskDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<DisplayTaskDto>> getAllTasks() {
        List<DisplayTaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу по ее идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно получена",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTaskDto.class))),
        @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayTaskDto> getTask(
        @Parameter(description = "Идентификатор задачи", example = "1") final @PathVariable Long id
    ) {
        DisplayTaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Задача успешно создана",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTaskDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DisplayTaskDto> createTask(
        @Parameter(description = "Данные для создания задачи") final @Valid @RequestBody CreateTaskDto dto
    ) {
        DisplayTaskDto createdTask = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(summary = "Обновить задачу", description = "Обновляет существующую задачу.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTaskDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content),
        @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayTaskDto> updateTask(
        @Parameter(description = "Идентификатор задачи для обновления", example = "1") final @PathVariable Long id,
        @Parameter(description = "Данные для обновления задачи") final @Valid @RequestBody CreateTaskDto dto
    ) {
        DisplayTaskDto updatedTask = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по ее идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Задача успешно удалена", content = @Content),
        @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
        @Parameter(description = "Идентификатор задачи для удаления", example = "1") final @PathVariable Long id
    ) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
