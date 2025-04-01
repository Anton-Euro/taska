package com.geml.taska.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.mapper.TagMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.User;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotebookRepository notebookRepository;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private NotebookService notebookService;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private CreateTagDto createTagDto;
    private DisplayTagDto displayTagDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");
        tag.setUser(user);

        createTagDto = new CreateTagDto();
        createTagDto.setName("Test Tag");
        createTagDto.setUserId(1L);

        displayTagDto = new DisplayTagDto();
        displayTagDto.setId(1L);
        displayTagDto.setName("Test Tag");
    }

    @Test
    void getAllTagsReturnsAllTags() {
        List<Tag> tags = List.of(tag);
        when(tagRepository.findAll()).thenReturn(tags);
        when(tagMapper.toDisplayTagDto(any(Tag.class))).thenReturn(displayTagDto);

        List<DisplayTagDto> result = tagService.getAllTags();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayTagDto, result.get(0));
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void getTagByIdExistingIdReturnsTag() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(tagMapper.toDisplayTagDto(any(Tag.class))).thenReturn(displayTagDto);

        DisplayTagDto result = tagService.getTagById(1L);

        assertNotNull(result);
        assertEquals(displayTagDto, result);
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void getTagByIdNonExistingIdThrowsNotFound() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> tagService.getTagById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void createTagValidDtoReturnsCreatedTag() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tagMapper.fromCreateTagDto(any(CreateTagDto.class))).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toDisplayTagDto(any(Tag.class))).thenReturn(displayTagDto);
        doNothing().when(notebookService).invalidateNotebookCache();

        DisplayTagDto result = tagService.createTag(createTagDto);

        assertNotNull(result);
        assertEquals(displayTagDto, result);
        verify(tagRepository, times(1)).save(any(Tag.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void createTagNonExistingUserThrowsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> tagService.createTag(createTagDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void createTagsBulkValidDtosReturnsCreatedTags() {
        List<CreateTagDto> createTagDtos = List.of(createTagDto);
        List<Tag> tags = List.of(tag);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tagMapper.fromCreateTagDto(any(CreateTagDto.class))).thenReturn(tag);
        when(tagRepository.saveAll(anyList())).thenReturn(tags);
        when(tagMapper.toDisplayTagDto(any(Tag.class))).thenReturn(displayTagDto);
        doNothing().when(notebookService).invalidateNotebookCache();

        List<DisplayTagDto> result = tagService.createTags(createTagDtos);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayTagDto, result.get(0));
        verify(tagRepository, times(1)).saveAll(anyList());
        verify(userRepository, times(1)).findById(anyLong());
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void createTagsBulkEmptyListThrowsException() {
        List<CreateTagDto> createTagDtos = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> tagService.createTags(createTagDtos));

        verify(tagRepository, never()).saveAll(anyList());
    }

    @Test
    void updateTagExistingIdReturnsUpdatedTag() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toDisplayTagDto(any(Tag.class))).thenReturn(displayTagDto);
        doNothing().when(notebookService).invalidateNotebookCache();

        DisplayTagDto result = tagService.updateTag(1L, createTagDto);

        assertNotNull(result);
        assertEquals(displayTagDto, result);
        verify(tagRepository, times(1)).save(any(Tag.class));
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void updateTagNonExistingIdThrowsNotFound() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> tagService.updateTag(1L, createTagDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void deleteTagExistingIdDeletesTag() {
        Notebook notebook = new Notebook();
        notebook.setId(1L);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        notebook.setTags(tags);
        Set<Notebook> notebooks = new HashSet<>();
        notebooks.add(notebook);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(notebookRepository.findByTagsId(anyLong())).thenReturn(notebooks);
        when(notebookRepository.save(any(Notebook.class))).thenReturn(notebook);
        doNothing().when(tagRepository).deleteById(anyLong());
        doNothing().when(notebookService).invalidateNotebookCache();

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(anyLong());
        verify(notebookRepository, times(1)).save(any(Notebook.class));
        verify(notebookService, times(1)).invalidateNotebookCache();
    }

    @Test
    void deleteTagNonExistingIdThrowsNotFound() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> tagService.deleteTag(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tagRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void deleteTagExistingIdWithNoNotebooksDeletesTag() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(notebookRepository.findByTagsId(anyLong())).thenReturn(new HashSet<>());
        doNothing().when(tagRepository).deleteById(anyLong());
        doNothing().when(notebookService).invalidateNotebookCache();

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(anyLong());
        verify(notebookRepository, never()).save(any(Notebook.class));
        verify(notebookService, times(1)).invalidateNotebookCache();
    }
}
