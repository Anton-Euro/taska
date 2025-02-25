package com.geml.taska.service;

import com.geml.taska.dto.CreateTaskItemDto;
import com.geml.taska.dto.DisplayTaskItemDto;
import com.geml.taska.mapper.TaskItemMapper;
import com.geml.taska.models.Task;
import com.geml.taska.models.TaskItem;
import com.geml.taska.repository.TaskItemRepository;
import com.geml.taska.repository.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


/**
 * Сервис для управления пунктами задач.
 */
@Service
public class TaskItemService {

  private final TaskItemRepository taskItemRepository;
  private final TaskItemMapper taskItemMapper;
  private final TaskRepository taskRepository;

  /**
   * Конструктор.
   *
   * @param taskItemRepository репозиторий пунктов задач
   * @param taskItemMapper маппер для TaskItem
   * @param taskRepository репозиторий задач
   */
  public TaskItemService(final TaskItemRepository taskItemRepository,
                         final TaskItemMapper taskItemMapper,
                         final TaskRepository taskRepository) {
    this.taskItemRepository = taskItemRepository;
    this.taskItemMapper = taskItemMapper;
    this.taskRepository = taskRepository;
  }

  /**
   * Получить все пункты задач.
   *
   * @return список TaskItemDto
   */
  public List<DisplayTaskItemDto> getAllTaskItems() {
    return taskItemRepository.findAll().stream()
        .map(taskItemMapper::toDisplayTaskItemDto)
        .collect(Collectors.toList());
  }

  /**
   * Получить пункт задачи по идентификатору.
   *
   * @param id идентификатор пункта задачи
   * @return TaskItemDto
   */
  public DisplayTaskItemDto getTaskItemById(final Long id) {
    TaskItem item = taskItemRepository.findById(id)
        .orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task item not found")
        );
    return taskItemMapper.toDisplayTaskItemDto(item);
  }

  /**
   * Создать пункт задачи.
   *
   * @param dto объект TaskItemDto
   * @return созданный TaskItemDto
   */
  public DisplayTaskItemDto createTaskItem(final CreateTaskItemDto dto) {
    TaskItem item = taskItemMapper.fromCreateTaskItemDto(dto);
    Task task = taskRepository.findById(dto.getTaskId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    item.setTask(task);
    TaskItem saved = taskItemRepository.save(item);
    return taskItemMapper.toDisplayTaskItemDto(saved);
  }

  /**
   * Обновить пункт задачи.
   *
   * @param id идентификатор пункта задачи
   * @param dto обновленные данные
   * @return обновленный TaskItemDto
   */
  public DisplayTaskItemDto updateTaskItem(final Long id, final CreateTaskItemDto dto) {
    TaskItem item = taskItemRepository.findById(id)
        .orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task item not found")
        );
    item.setTitle(dto.getTitle());
    item.setCompleted(dto.getCompleted());
    TaskItem saved = taskItemRepository.save(item);
    return taskItemMapper.toDisplayTaskItemDto(saved);
  }

  /**
   * Удалить пункт задачи.
   *
   * @param id идентификатор пункта задачи
   */
  public void deleteTaskItem(final Long id) {
    if (!taskItemRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task item not found");
    }
    taskItemRepository.deleteById(id);
  }
}