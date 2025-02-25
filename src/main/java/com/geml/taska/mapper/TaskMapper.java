package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.models.Task;
import org.springframework.stereotype.Component;

/**
 * Mapper для сущности Task.
 */
@Component
public class TaskMapper {

  /**
   * Преобразование Task в TaskDTO.
   *
   * @param task сущность задачи
   * @return TaskDTO
   */
  public DisplayTaskDto toDisplayTaskDto(final Task task) {
    return new DisplayTaskDto(task.getId(), task.getTitle(), task.getDescription(),
        task.getUser().getId());
  }

  /**
   * Преобразование TaskDTO в Task.
   *
   * @param dto объект TaskDTO
   * @return сущность Task
   */
  public Task fromCreateTaskDto(final CreateTaskDto dto) {
    Task task = new Task();
    task.setTitle(dto.getTitle());
    task.setDescription(dto.getDescription());
    return task;
  }
}
