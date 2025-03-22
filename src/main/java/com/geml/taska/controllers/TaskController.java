package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;


    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping
    public ResponseEntity<List<DisplayTaskDto>> getAllTasks() {
        List<DisplayTaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisplayTaskDto> getTask(final @PathVariable Long id) {
        DisplayTaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }


    @PostMapping
    public ResponseEntity<DisplayTaskDto> createTask(
        final @Valid @RequestBody CreateTaskDto dto
    ) {
        DisplayTaskDto createdTask = taskService.createTask(dto);
        return ResponseEntity.status(201).body(createdTask);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisplayTaskDto> updateTask(
        final @PathVariable Long id,
        final @Valid @RequestBody CreateTaskDto dto
    ) {
        DisplayTaskDto updatedTask = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(final @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
