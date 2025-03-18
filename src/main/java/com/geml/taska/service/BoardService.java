package com.geml.taska.service;

import com.geml.taska.dto.CreateBoardDto;
import com.geml.taska.dto.DisplayBoardDto;
import com.geml.taska.mapper.BoardMapper;
import com.geml.taska.models.Board;
import com.geml.taska.models.Task;
import com.geml.taska.models.User;
import com.geml.taska.repository.BoardRepository;
import com.geml.taska.repository.TaskRepository;
import com.geml.taska.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;


    public BoardService(
        final BoardRepository boardRepository,
        final BoardMapper boardMapper,
        final TaskRepository taskRepository,
        final UserRepository userRepository,
        final TaskService taskService 
    ) {
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }


    public List<DisplayBoardDto> getAllBoards(final String title) {
        List<Board> tasks = (title != null && !title.isEmpty())
            ? boardRepository.searchByTitle(title)
            : boardRepository.findAll();

        return tasks.stream()
                .map(boardMapper::toDisplayBoardDto).toList();
    }


    public DisplayBoardDto getBoardById(final Long id) {
        Board task = boardRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
            );
        return boardMapper.toDisplayBoardDto(task);
    }


    public DisplayBoardDto createBoard(final CreateBoardDto taskDto) {
        Board board = boardMapper.fromCreateBoardDto(taskDto);
        User user = userRepository.findById(taskDto.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        board.setUser(user);
        Board saved = boardRepository.save(board);
        return boardMapper.toDisplayBoardDto(saved);
    }


    public DisplayBoardDto updateBoard(final Long id, final CreateBoardDto taskDto) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        board.setTitle(taskDto.getTitle());
        board.setDescription(taskDto.getDescription());
        Board saved = boardRepository.save(board);
        return boardMapper.toDisplayBoardDto(saved);
    }

    @Transactional
    public void deleteBoard(final Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task board not found")
        );

        List<Task> tasks = taskRepository.findByBoardId(id);

        tasks.forEach(task -> {
            taskService.deleteTask(task.getId());
        });
        boardRepository.delete(board);
    }
}