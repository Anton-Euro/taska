package com.geml.taska.mapper;

import com.geml.taska.dto.CreateBoardDto;
import com.geml.taska.dto.DisplayBoardDto;
import com.geml.taska.models.Board;
import org.springframework.stereotype.Component;


@Component
public class BoardMapper {


    public DisplayBoardDto toDisplayBoardDto(final Board task) {
        return new DisplayBoardDto(
            task.getId(), 
            task.getTitle(), 
            task.getDescription(),
            task.getUser().getId()
        );
    }


    public Board fromCreateBoardDto(final CreateBoardDto dto) {
        Board task = new Board();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        return task;
    }
}
