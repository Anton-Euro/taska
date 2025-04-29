package com.geml.taska.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.geml.taska.dto.CreateBoardDto;
import com.geml.taska.dto.DisplayBoardDto;
import com.geml.taska.mapper.BoardMapper;
import com.geml.taska.models.Board;
import com.geml.taska.models.Task;
import com.geml.taska.models.User;
import com.geml.taska.repository.BoardRepository;
import com.geml.taska.repository.TaskRepository;
import com.geml.taska.repository.UserRepository;
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
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private BoardService boardService;

    private Board board;
    private CreateBoardDto createBoardDto;
    private DisplayBoardDto displayBoardDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        board = new Board();
        board.setId(1L);
        board.setTitle("Test Board");
        board.setDescription("Test Description");
        board.setUser(user);

        createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("Test Board");
        createBoardDto.setDescription("Test Description");
        createBoardDto.setUserId(1L);

        displayBoardDto = new DisplayBoardDto();
        displayBoardDto.setId(1L);
        displayBoardDto.setTitle("Test Board");
        displayBoardDto.setDescription("Test Description");
    }

    @Test
    void getAllBoardsNoFilterReturnsAllBoards() {
        List<Board> boards = List.of(board);
        when(boardRepository.findAll()).thenReturn(boards);
        when(boardMapper.toDisplayBoardDto(any(Board.class))).thenReturn(displayBoardDto);

        List<DisplayBoardDto> result = boardService.getAllBoards(null);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(displayBoardDto, result.get(0));
        verify(boardRepository, times(1)).findAll();
        verify(boardRepository, never()).searchByTitle(anyString());
    }

    @Test
    void getAllBoardsWithFilterReturnsFilteredBoards() {
        List<Board> boards = List.of(board);
        when(boardRepository.searchByTitle(anyString())).thenReturn(boards);
        when(boardMapper.toDisplayBoardDto(any(Board.class))).thenReturn(displayBoardDto);

        List<DisplayBoardDto> result = boardService.getAllBoards("Test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayBoardDto, result.get(0));
        verify(boardRepository, times(1)).searchByTitle(anyString());
        verify(boardRepository, never()).findAll();
    }

    @Test
    void getBoardByIdExistingIdReturnsBoard() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(boardMapper.toDisplayBoardDto(any(Board.class))).thenReturn(displayBoardDto);

        DisplayBoardDto result = boardService.getBoardById(1L);

        assertNotNull(result);
        assertEquals(displayBoardDto, result);
        verify(boardRepository, times(1)).findById(1L);
    }

    @Test
    void getBoardByIdNonExistingIdThrowsNotFound() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> boardService.getBoardById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(boardRepository, times(1)).findById(1L);
    }

    @Test
    void createBoardValidDtoReturnsCreatedBoard() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(boardMapper.fromCreateBoardDto(any(CreateBoardDto.class))).thenReturn(board);
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(boardMapper.toDisplayBoardDto(any(Board.class))).thenReturn(displayBoardDto);

        DisplayBoardDto result = boardService.createBoard(createBoardDto);

        assertNotNull(result);
        assertEquals(displayBoardDto, result);
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void createBoardNonExistingUserThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> boardService.createBoard(createBoardDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(boardRepository, never()).save(any(Board.class));
    }

    @Test
    void updateBoardExistingIdReturnsUpdatedBoard() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(boardMapper.toDisplayBoardDto(any(Board.class))).thenReturn(displayBoardDto);

        DisplayBoardDto result = boardService.updateBoard(1L, createBoardDto);

        assertNotNull(result);
        assertEquals(displayBoardDto, result);
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    void updateBoardNonExistingIdThrowsNotFound() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> boardService.updateBoard(1L, createBoardDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(boardRepository, never()).save(any(Board.class));
    }

    @Test
    void deleteBoardExistingIdDeletesBoard() {
        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setId(1L);
        tasks.add(task);
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(taskRepository.findByBoardId(anyLong())).thenReturn(tasks);
        doNothing().when(taskService).deleteTask(anyLong());
        doNothing().when(boardRepository).delete(any(Board.class));

        boardService.deleteBoard(1L);

        verify(boardRepository, times(1)).delete(any(Board.class));
        verify(taskService, times(1)).deleteTask(anyLong());
    }

    @Test
    void deleteBoardNonExistingIdThrowsNotFound() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> boardService.deleteBoard(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(boardRepository, never()).delete(any(Board.class));
    }
    
    @Test
    void deleteBoardExistingIdWithNoTasksDeletesBoard() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(taskRepository.findByBoardId(anyLong())).thenReturn(new ArrayList<>());
        doNothing().when(boardRepository).delete(any(Board.class));

        boardService.deleteBoard(1L);

        verify(boardRepository, times(1)).delete(any(Board.class));
        verify(taskService, never()).deleteTask(anyLong());
    }
}
