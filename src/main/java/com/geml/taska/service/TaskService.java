package com.geml.taska.service;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.mapper.TaskMapper;
import com.geml.taska.models.Task;
import com.geml.taska.models.User;
import com.geml.taska.repository.TaskRepository;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Сервис для управления задачами.
 */
@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;
  private final UserRepository userRepository;

  /**
   * Конструктор.
   *
   * @param taskRepository репозиторий задач
   * @param taskMapper маппер для Task
   * @param userRepository репозиторий пользователей
   */
  public TaskService(final TaskRepository taskRepository,
                     final TaskMapper taskMapper,
                     final UserRepository userRepository) {
    this.taskRepository = taskRepository;
    this.taskMapper = taskMapper;
    this.userRepository = userRepository;
  }

  /**
   * Получить все задачи.
   *
   * @return список TaskDto
   */
  public List<DisplayTaskDto> getAllTasks(String title) {
    List<Task> tasks = (title != null && !title.isEmpty())
        ? taskRepository.searchByTitle(title)
        : taskRepository.findAll();

    return tasks.stream()
                .map(taskMapper::toDisplayTaskDto)
                .collect(Collectors.toList());
  }

  /**
   * Получить задачу по идентификатору.
   *
   * @param id идентификатор задачи
   * @return TaskDto
   */
  public DisplayTaskDto getTaskById(final Long id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    return taskMapper.toDisplayTaskDto(task);
  }

  /**
   * Создать задачу.
   *
   * @param taskDto объект TaskDto
   * @return созданный TaskDto
   */
  public DisplayTaskDto createTask(final CreateTaskDto taskDto) {
    Task task = taskMapper.fromCreateTaskDto(taskDto);
    User user = userRepository.findById(taskDto.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    task.setUser(user);
    Task saved = taskRepository.save(task);
    return taskMapper.toDisplayTaskDto(saved);
  }

  /**
   * Обновить задачу.
   *
   * @param id идентификатор задачи
   * @param taskDto обновленные данные
   * @return обновленный TaskDto
   */
  public DisplayTaskDto updateTask(final Long id, final CreateTaskDto taskDto) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    task.setTitle(taskDto.getTitle());
    task.setDescription(taskDto.getDescription());
    Task saved = taskRepository.save(task);
    return taskMapper.toDisplayTaskDto(saved);
  }

  /**
   * Удалить задачу.
   *
   * @param id идентификатор задачи
   */
  public void deleteTask(final Long id) {
    if (!taskRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    taskRepository.deleteById(id);
  }
}