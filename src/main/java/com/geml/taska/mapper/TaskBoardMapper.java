package com.geml.taska.mapper;

import com.geml.taska.dto.CreateTaskBoardDto;
import com.geml.taska.dto.DisplayTaskBoardDto;
import com.geml.taska.models.TaskBoard;
import org.springframework.stereotype.Component;


@Component
public class TaskBoardMapper {


    public DisplayTaskBoardDto toDisplayTaskBoardDto(final TaskBoard task) {
        return new DisplayTaskBoardDto(
            task.getId(), 
            task.getTitle(), 
            task.getDescription(),
            task.getUser().getId()
        );
    }


    public TaskBoard fromCreateTaskBoardDto(final CreateTaskBoardDto dto) {
        TaskBoard task = new TaskBoard();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        return task;
    }
}
