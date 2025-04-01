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

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.exception.CustomNotFoundException;
import com.geml.taska.mapper.UserMapper;
import com.geml.taska.models.Board;
import com.geml.taska.models.Tag;
import com.geml.taska.models.User;
import com.geml.taska.repository.BoardRepository;
import com.geml.taska.repository.TagRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BoardService boardService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserDto createUserDto;
    private DisplayUserDto displayUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        createUserDto = new CreateUserDto();
        createUserDto.setUsername("testuser");
        createUserDto.setEmail("test@example.com");
        createUserDto.setPassword("password");

        displayUserDto = new DisplayUserDto();
        displayUserDto.setId(1L);
        displayUserDto.setUsername("testuser");
        displayUserDto.setEmail("test@example.com");
    }

    @Test
    void getAllUsersReturnsAllUsers() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDisplayUserDto(any(User.class))).thenReturn(displayUserDto);

        List<DisplayUserDto> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayUserDto, result.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdExistingIdReturnsUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDisplayUserDto(any(User.class))).thenReturn(displayUserDto);

        DisplayUserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(displayUserDto, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByIdNonExistingIdThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomNotFoundException exception = assertThrows(CustomNotFoundException.class, () -> userService.getUserById(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createUserValidDtoReturnsCreatedUser() {
        when(userMapper.fromCreateUserDto(any(CreateUserDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDisplayUserDto(any(User.class))).thenReturn(displayUserDto);

        DisplayUserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(displayUserDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserExistingIdReturnsUpdatedUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDisplayUserDto(any(User.class))).thenReturn(displayUserDto);

        DisplayUserDto result = userService.updateUser(1L, createUserDto);

        assertNotNull(result);
        assertEquals(displayUserDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserNonExistingIdThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, createUserDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserExistingIdDeletesUser() {
        List<Board> boards = new ArrayList<>();
        Board board = new Board();
        board.setId(1L);
        boards.add(board);
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setId(1L);
        tags.add(tag);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(boardRepository.findByUserId(anyLong())).thenReturn(boards);
        when(tagRepository.findByUserId(anyLong())).thenReturn(tags);
        doNothing().when(boardService).deleteBoard(anyLong());
        doNothing().when(tagService).deleteTag(anyLong());
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(any(User.class));
        verify(boardService, times(1)).deleteBoard(anyLong());
        verify(tagService, times(1)).deleteTag(anyLong());
    }

    @Test
    void deleteUserNonExistingIdThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.deleteUser(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(userRepository, never()).delete(any(User.class));
    }
    
    @Test
    void deleteUserExistingIdWithNoBoardsAndTagsDeletesUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(boardRepository.findByUserId(anyLong())).thenReturn(new ArrayList<>());
        when(tagRepository.findByUserId(anyLong())).thenReturn(new ArrayList<>());
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(any(User.class));
        verify(boardService, never()).deleteBoard(anyLong());
        verify(tagService, never()).deleteTag(anyLong());
    }
}
