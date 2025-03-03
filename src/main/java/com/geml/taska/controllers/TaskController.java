package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.service.TaskService;
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
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;


    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping
    public ResponseEntity<List<DisplayTaskDto>> getAllTasks(
        @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(taskService.getAllTasks(title));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisplayTaskDto> getTask(@PathVariable Long id) {
        DisplayTaskDto taskDto = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDto);
    }


    @PostMapping
    public ResponseEntity<DisplayTaskDto> createTask(@RequestBody CreateTaskDto taskDto) {
        DisplayTaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisplayTaskDto> updateTask(
        @PathVariable Long id,
        @RequestBody CreateTaskDto taskDto
    ) {
        DisplayTaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}