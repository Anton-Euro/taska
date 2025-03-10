package com.geml.taska.service;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.mapper.TaskMapper;
import com.geml.taska.models.Task;
import com.geml.taska.models.User;
import com.geml.taska.repository.TaskRepository;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;


    public TaskService(final TaskRepository taskRepository,
            final TaskMapper taskMapper,
            final UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
    }


    public List<DisplayTaskDto> getAllTasks(final String title) {
        List<Task> tasks = (title != null && !title.isEmpty())
            ? taskRepository.searchByTitle(title)
            : taskRepository.findAll();

        return tasks.stream()
                .map(taskMapper::toDisplayTaskDto).toList();
    }


    public DisplayTaskDto getTaskById(final Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
            );
        return taskMapper.toDisplayTaskDto(task);
    }


    public DisplayTaskDto createTask(final CreateTaskDto taskDto) {
        Task task = taskMapper.fromCreateTaskDto(taskDto);
        User user = userRepository.findById(taskDto.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        task.setUser(user);
        Task saved = taskRepository.save(task);
        return taskMapper.toDisplayTaskDto(saved);
    }


    public DisplayTaskDto updateTask(final Long id, final CreateTaskDto taskDto) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        Task saved = taskRepository.save(task);
        return taskMapper.toDisplayTaskDto(saved);
    }


    public void deleteTask(final Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        taskRepository.deleteById(id);
    }
}