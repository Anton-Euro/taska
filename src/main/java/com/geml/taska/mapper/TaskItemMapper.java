package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTaskItemDto;
import com.geml.taska.dto.DisplayTaskItemDto;
import com.geml.taska.models.TaskItem;
import org.springframework.stereotype.Component;

/**
 * Mapper для сущности TaskItem.
 */
@Component
public class TaskItemMapper {

  /**
   * Преобразование TaskItem в TaskItemDTO.
   *
   * @param item сущность пункта задачи
   * @return TaskItemDTO
   */
  public DisplayTaskItemDto toDisplayTaskItemDto(final TaskItem item) {
    return new DisplayTaskItemDto(item.getId(), item.getTitle(), item.getCompleted(),
        item.getTask().getId());
  }

  /**
   * Преобразование TaskItemDTO в TaskItem.
   *
   * @param dto объект TaskItemDTO
   * @return сущность TaskItem
   */
  public TaskItem fromCreateTaskItemDto(final CreateTaskItemDto dto) {
    TaskItem item = new TaskItem();
    item.setTitle(dto.getTitle());
    item.setCompleted(dto.getCompleted());
    return item;
  }
}
