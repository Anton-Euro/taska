package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTaskItemDto;
import com.geml.taska.dto.DisplayTaskItemDto;
import com.geml.taska.service.TaskItemService;
import java.util.List;
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
 * REST контроллер для управления пунктами задач.
 */
@RestController
@RequestMapping("/api/task-items")
public class TaskItemController {

  private final TaskItemService taskItemService;

  /**
   * Конструктор.
   *
   * @param taskItemService сервис пунктов задач
   */
  public TaskItemController(final TaskItemService taskItemService) {
    this.taskItemService = taskItemService;
  }

  /**
   * Получить все пункты задач.
   *
   * @return список TaskItemDto
   */
  @GetMapping
  public ResponseEntity<List<DisplayTaskItemDto>> getAllTaskItems() {
    List<DisplayTaskItemDto> taskItems = taskItemService.getAllTaskItems();
    return ResponseEntity.ok(taskItems);
  }

  /**
   * Получить пункт задачи по id.
   *
   * @param id идентификатор пункта задачи
   * @return TaskItemDto
   */
  @GetMapping("/{id}")
  public ResponseEntity<DisplayTaskItemDto> getTaskItem(final @PathVariable Long id) {
    DisplayTaskItemDto taskItem = taskItemService.getTaskItemById(id);
    return ResponseEntity.ok(taskItem);
  }

  /**
   * Создать пункт задачи.
   *
   * @param dto объект TaskItemDto
   * @return созданный TaskItemDto
   */
  @PostMapping
  public ResponseEntity<DisplayTaskItemDto> createTaskItem(final @RequestBody CreateTaskItemDto dto
  ) {
    DisplayTaskItemDto createdTaskItem = taskItemService.createTaskItem(dto);
    return ResponseEntity.status(201).body(createdTaskItem);
  }

  /**
   * Обновить пункт задачи.
   *
   * @param id идентификатор пункта задачи
   * @param dto объект TaskItemDto
   * @return обновленный TaskItemDto
   */
  @PutMapping("/{id}")
  public ResponseEntity<DisplayTaskItemDto> updateTaskItem(final @PathVariable Long id,
                                                    final @RequestBody CreateTaskItemDto dto) {
    DisplayTaskItemDto updatedTaskItem = taskItemService.updateTaskItem(id, dto);
    return ResponseEntity.ok(updatedTaskItem);
  }

  /**
   * Удалить пункт задачи.
   *
   * @param id идентификатор пункта задачи
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTaskItem(final @PathVariable Long id) {
    taskItemService.deleteTaskItem(id);
    return ResponseEntity.noContent().build();
  }
}