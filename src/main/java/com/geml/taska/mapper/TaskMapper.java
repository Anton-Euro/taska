package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.models.Task;
import org.springframework.stereotype.Component;


@Component
public class TaskMapper {


    public DisplayTaskDto toDisplayTaskDto(final Task task) {
        return new DisplayTaskDto(task.getId(), task.getTitle(), task.getDescription(),
                task.getUser().getId());
    }


    public Task fromCreateTaskDto(final CreateTaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        return task;
    }
}
