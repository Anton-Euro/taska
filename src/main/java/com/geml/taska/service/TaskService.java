package com.geml.taska.service;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.mapper.TaskMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Task;
import com.geml.taska.models.TaskBoard;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TaskBoardRepository;
import com.geml.taska.repository.TaskRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final NotebookRepository notebookRepository;
    private final TaskBoardRepository taskBoardRepository;
    private final NotebookService notebookService;


    public TaskService(
        final TaskRepository taskRepository,
        final TaskMapper taskMapper,
        final NotebookRepository notebookRepository,
        final TaskBoardRepository taskBoardRepository,
        final NotebookService notebookService    
    ) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.notebookRepository = notebookRepository;
        this.taskBoardRepository = taskBoardRepository;
        this.notebookService = notebookService;
    }


    public List<DisplayTaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
            .map(taskMapper::toDisplayTaskDto)
            .collect(Collectors.toList());
    }


    public DisplayTaskDto getTaskById(final Long id) {
        Task item = taskRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        return taskMapper.toDisplayTaskDto(item);
    }


    public DisplayTaskDto createTask(final CreateTaskDto dto) {
        Task item = taskMapper.fromCreateTaskItemDto(dto);
        TaskBoard task = taskBoardRepository.findById(dto.getTaskBoardId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        item.setTaskBoard(task);
        Task saved = taskRepository.save(item);
        return taskMapper.toDisplayTaskDto(saved);
    }


    public DisplayTaskDto updateTask(final Long id, final CreateTaskDto dto) {
        Task item = taskRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        item.setTitle(dto.getTitle());
        item.setCompleted(dto.getCompleted());
        Task saved = taskRepository.save(item);
        return taskMapper.toDisplayTaskDto(saved);
    }

    @Transactional
    public void deleteTask(final Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        );

        List<Notebook> notebooks = notebookRepository.findByTaskId(id);

        notebooks.forEach(notebook -> {
            notebookService.deleteNotebook(notebook.getId());
        });

        taskRepository.delete(task);
    }
}