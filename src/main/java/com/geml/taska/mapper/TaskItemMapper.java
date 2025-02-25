package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTaskItemDto;
import com.geml.taska.dto.DisplayTaskItemDto;
import com.geml.taska.models.TaskItem;
import org.springframework.stereotype.Component;


@Component
public class TaskItemMapper {


    public DisplayTaskItemDto toDisplayTaskItemDto(final TaskItem item) {
        return new DisplayTaskItemDto(item.getId(), item.getTitle(), item.getCompleted(),
                item.getTask().getId());
    }


    public TaskItem fromCreateTaskItemDto(final CreateTaskItemDto dto) {
        TaskItem item = new TaskItem();
        item.setTitle(dto.getTitle());
        item.setCompleted(dto.getCompleted());
        return item;
    }
}
