package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.service.TaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для управления задачами.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  /**
   * Конструктор.
   *
   * @param taskService сервис задач
   */
  public TaskController(final TaskService taskService) {
    this.taskService = taskService;
  }

  /**
   * Получить все задачи.
   *
   * @return список TaskDto
   */
  @GetMapping
  public ResponseEntity<List<DisplayTaskDto>> getAllTasks() {
    List<DisplayTaskDto> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(tasks);
  }

  /**
   * Получить задачу по id.
   *
   * @param id идентификатор задачи
   * @return TaskDto
   */
  @GetMapping("/id")
  public ResponseEntity<DisplayTaskDto> getTask(@RequestParam Long id) {
    DisplayTaskDto taskDto = taskService.getTaskById(id);
    return ResponseEntity.ok(taskDto);
  }

  /**
   * Создать задачу.
   *
   * @param taskDto объект TaskDto
   * @return созданный TaskDto
   */
  @PostMapping
  public ResponseEntity<DisplayTaskDto> createTask(@RequestBody CreateTaskDto taskDto) {
    DisplayTaskDto createdTask = taskService.createTask(taskDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

  /**
   * Обновить задачу.
   *
   * @param id идентификатор задачи
   * @param taskDto объект TaskDto
   * @return обновленный TaskDto
   */
  @PutMapping("/{id}")
  public ResponseEntity<DisplayTaskDto> updateTask(@PathVariable Long id, 
                                                  @RequestBody CreateTaskDto taskDto) {
    DisplayTaskDto updatedTask = taskService.updateTask(id, taskDto);
    return ResponseEntity.ok(updatedTask);
  }

  /**
   * Удалить задачу.
   *
   * @param id идентификатор задачи
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}