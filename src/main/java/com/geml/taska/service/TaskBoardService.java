package com.geml.taska.service;

import com.geml.taska.dto.CreateTaskBoardDto;
import com.geml.taska.dto.DisplayTaskBoardDto;
import com.geml.taska.mapper.TaskBoardMapper;
import com.geml.taska.models.Task;
import com.geml.taska.models.TaskBoard;
import com.geml.taska.models.User;
import com.geml.taska.repository.TaskBoardRepository;
import com.geml.taska.repository.TaskRepository;
import com.geml.taska.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class TaskBoardService {

    private final TaskBoardRepository taskBoardRepository;
    private final TaskBoardMapper taskBoardMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;


    public TaskBoardService(
        final TaskBoardRepository taskBoardRepository,
        final TaskBoardMapper taskBoardMapper,
        final TaskRepository taskRepository,
        final UserRepository userRepository,
        final TaskService taskService 
    ) {
        this.taskBoardRepository = taskBoardRepository;
        this.taskBoardMapper = taskBoardMapper;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }


    public List<DisplayTaskBoardDto> getAllTaskBoards(final String title) {
        List<TaskBoard> tasks = (title != null && !title.isEmpty())
            ? taskBoardRepository.searchByTitle(title)
            : taskBoardRepository.findAll();

        return tasks.stream()
                .map(taskBoardMapper::toDisplayTaskBoardDto).toList();
    }


    public DisplayTaskBoardDto getTaskBoardById(final Long id) {
        TaskBoard task = taskBoardRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
            );
        return taskBoardMapper.toDisplayTaskBoardDto(task);
    }


    public DisplayTaskBoardDto createTaskBoard(final CreateTaskBoardDto taskDto) {
        TaskBoard taskBoard = taskBoardMapper.fromCreateTaskBoardDto(taskDto);
        User user = userRepository.findById(taskDto.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        taskBoard.setUser(user);
        TaskBoard saved = taskBoardRepository.save(taskBoard);
        return taskBoardMapper.toDisplayTaskBoardDto(saved);
    }


    public DisplayTaskBoardDto updateTaskBoard(final Long id, final CreateTaskBoardDto taskDto) {
        TaskBoard taskBoard = taskBoardRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        taskBoard.setTitle(taskDto.getTitle());
        taskBoard.setDescription(taskDto.getDescription());
        TaskBoard saved = taskBoardRepository.save(taskBoard);
        return taskBoardMapper.toDisplayTaskBoardDto(saved);
    }

    @Transactional
    public void deleteTaskBoard(final Long id) {
        TaskBoard taskBoard = taskBoardRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task board not found")
        );

        List<Task> tasks = taskRepository.findByTaskBoardId(id);

        tasks.forEach(task -> {
            taskService.deleteTask(task.getId());
        });
        taskBoardRepository.delete(taskBoard);
    }
}