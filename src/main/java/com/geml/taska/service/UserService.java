package com.geml.taska.service;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.mapper.UserMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.TaskBoard;
import com.geml.taska.models.User;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.TaskBoardRepository;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskBoardRepository taskBoardRepository;
    private final TagRepository tagRepository;
    private final NotebookRepository notebookRepository;
    private final TaskBoardService taskBoardService;
    private final TagService tagService;
    private final NotebookService notebookService;


    public UserService(
        final UserRepository userRepository,
        final UserMapper userMapper,
        final TaskBoardRepository taskBoardRepository,
        final TagRepository tagRepository,
        final NotebookRepository notebookRepository,
        final TaskBoardService taskBoardService,
        final TagService tagService,
        final NotebookService notebookService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.taskBoardRepository = taskBoardRepository;
        this.tagRepository = tagRepository;
        this.notebookRepository = notebookRepository;
        this.taskBoardService = taskBoardService;
        this.tagService = tagService;
        this.notebookService = notebookService;
    }


    public List<DisplayUserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toDisplayUserDto)
            .collect(Collectors.toList());
    }


    public DisplayUserDto getUserById(final Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return userMapper.toDisplayUserDto(user);
    }


    public DisplayUserDto createUser(final CreateUserDto userDto) {
        User user = userMapper.fromCreateUserDto(userDto);
        User saved = userRepository.save(user);
        return userMapper.toDisplayUserDto(saved);
    }


    public DisplayUserDto updateUser(final Long id, final CreateUserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        User saved = userRepository.save(user);
        return userMapper.toDisplayUserDto(saved);
    }


    @Transactional
    public void deleteUser(final Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        List<TaskBoard> taskBoards = taskBoardRepository.findByUserId(id);
        taskBoards.forEach(taskBoard -> {
            taskBoardService.deleteTaskBoard(taskBoard.getId());
        });

        List<Tag> tags = tagRepository.findByUserId(id);
        tags.forEach(tag -> {
            tagService.deleteTag(tag.getId());
        });

        List<Notebook> notebooks = notebookRepository.findByUserId(id);
        notebooks.forEach(notebook -> {
            notebookService.deleteNotebook(notebook.getId());
        });

        userRepository.delete(user);
    }
}