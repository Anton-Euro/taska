package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTaskBoardDto;
import com.geml.taska.dto.DisplayTaskBoardDto;
import com.geml.taska.service.TaskBoardService;
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


@RestController
@RequestMapping("/api/task-boards")
public class TaskBoardController {

    private final TaskBoardService taskBoardService;


    public TaskBoardController(final TaskBoardService taskBoardService) {
        this.taskBoardService = taskBoardService;
    }


    @GetMapping
    public ResponseEntity<List<DisplayTaskBoardDto>> getAllTaskBoards(
        @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(taskBoardService.getAllTaskBoards(title));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisplayTaskBoardDto> getTaskBoard(@PathVariable Long id) {
        DisplayTaskBoardDto taskBoardDto = taskBoardService.getTaskBoardById(id);
        return ResponseEntity.ok(taskBoardDto);
    }


    @PostMapping
    public ResponseEntity<DisplayTaskBoardDto> createTaskBoard(
        @RequestBody CreateTaskBoardDto taskBoardDto
    ) {
        DisplayTaskBoardDto createdTaskBoard = taskBoardService.createTaskBoard(taskBoardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskBoard);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisplayTaskBoardDto> updateTaskBoard(
        @PathVariable Long id,
        @RequestBody CreateTaskBoardDto taskBoardDto
    ) {
        DisplayTaskBoardDto updatedTaskBoard = taskBoardService.updateTaskBoard(id, taskBoardDto);
        return ResponseEntity.ok(updatedTaskBoard);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskBoardService.deleteTaskBoard(id);
        return ResponseEntity.noContent().build();
    }
}