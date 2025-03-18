package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.models.Task;
import org.springframework.stereotype.Component;


@Component
public class TaskMapper {


    public DisplayTaskDto toDisplayTaskDto(final Task item) {
        return new DisplayTaskDto(
            item.getId(), 
            item.getTitle(), 
            item.getCompleted(),
            item.getBoard().getId()
        );
    }


    public Task fromCreateTaskItemDto(final CreateTaskDto dto) {
        Task item = new Task();
        item.setTitle(dto.getTitle());
        item.setCompleted(dto.getCompleted());
        return item;
    }
}
