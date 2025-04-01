package com.geml.taska.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.geml.taska.dto.CreateTaskDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.mapper.TaskMapper;
import com.geml.taska.models.Board;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Task;
import com.geml.taska.repository.BoardRepository;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private NotebookRepository notebookRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private NotebookService notebookService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private CreateTaskDto createTaskDto;
    private DisplayTaskDto displayTaskDto;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.setId(1L);
        board.setTitle("Test Board");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setCompleted(false);
        task.setBoard(board);

        createTaskDto = new CreateTaskDto();
        createTaskDto.setTitle("Test Task");
        createTaskDto.setCompleted(false);
        createTaskDto.setBoardId(1L);

        displayTaskDto = new DisplayTaskDto();
        displayTaskDto.setId(1L);
        displayTaskDto.setTitle("Test Task");
        displayTaskDto.setCompleted(false);
    }

    @Test
    void getAllTasksReturnsAllTasks() {
        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toDisplayTaskDto(any(Task.class))).thenReturn(displayTaskDto);

        List<DisplayTaskDto> result = taskService.getAllTasks();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayTaskDto, result.get(0));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskByIdExistingIdReturnsTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toDisplayTaskDto(any(Task.class))).thenReturn(displayTaskDto);

        DisplayTaskDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(displayTaskDto, result);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskByIdNonExistingIdThrowsNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> taskService.getTaskById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void createTaskValidDtoReturnsCreatedTask() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(taskMapper.fromCreateTaskItemDto(any(CreateTaskDto.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDisplayTaskDto(any(Task.class))).thenReturn(displayTaskDto);
        doNothing().when(notebookService).invalidateNotebookCache();

        DisplayTaskDto result = taskService.createTask(createTaskDto);

        assertNotNull(result);
        assertEquals(displayTaskDto, result);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(boardRepository, times(1)).findById(anyLong());
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void createTaskNonExistingBoardThrowsNotFound() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> taskService.createTask(createTaskDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTaskExistingIdReturnsUpdatedTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDisplayTaskDto(any(Task.class))).thenReturn(displayTaskDto);
        doNothing().when(notebookService).invalidateNotebookCache();

        DisplayTaskDto result = taskService.updateTask(1L, createTaskDto);

        assertNotNull(result);
        assertEquals(displayTaskDto, result);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void updateTaskNonExistingIdThrowsNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> taskService.updateTask(1L, createTaskDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTaskExistingIdDeletesTask() {
        List<Notebook> notebooks = new ArrayList<>();
        Notebook notebook = new Notebook();
        notebook.setId(1L);
        notebooks.add(notebook);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(notebookRepository.findByTaskId(anyLong())).thenReturn(notebooks);
        doNothing().when(notebookService).deleteNotebook(anyLong());
        doNothing().when(taskRepository).delete(any(Task.class));
        doNothing().when(notebookService).invalidateNotebookCache();

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(any(Task.class));
        verify(notebookService, times(1)).deleteNotebook(anyLong());
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void deleteTaskNonExistingIdThrowsNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> taskService.deleteTask(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(taskRepository, never()).delete(any(Task.class));
    }
    
    @Test
    void deleteTaskExistingIdWithNoNotebooksDeletesTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(notebookRepository.findByTaskId(anyLong())).thenReturn(new ArrayList<>());
        doNothing().when(taskRepository).delete(any(Task.class));
        doNothing().when(notebookService).invalidateNotebookCache();

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(any(Task.class));
        verify(notebookService, never()).deleteNotebook(anyLong());
        verify(notebookService, times(1)).invalidateNotebookCache();
    }
}
