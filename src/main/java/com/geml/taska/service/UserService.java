package com.geml.taska.service;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.mapper.UserMapper;
import com.geml.taska.models.Board;
import com.geml.taska.models.Tag;
import com.geml.taska.models.User;
import com.geml.taska.repository.BoardRepository;
import com.geml.taska.repository.TagRepository;
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
    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;
    private final BoardService boardService;
    private final TagService tagService;


    public UserService(
        final UserRepository userRepository,
        final UserMapper userMapper,
        final BoardRepository boardRepository,
        final TagRepository tagRepository,
        final BoardService boardService,
        final TagService tagService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.boardRepository = boardRepository;
        this.tagRepository = tagRepository;
        this.boardService = boardService;
        this.tagService = tagService;
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

        List<Board> taskBoards = boardRepository.findByUserId(id);
        taskBoards.forEach(taskBoard -> {
            boardService.deleteBoard(taskBoard.getId());
        });

        List<Tag> tags = tagRepository.findByUserId(id);
        tags.forEach(tag -> {
            tagService.deleteTag(tag.getId());
        });

        userRepository.delete(user);
    }
}