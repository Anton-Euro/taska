package com.geml.taska.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.geml.taska.config.CacheConfig;
import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import com.geml.taska.exception.ValidationException;
import com.geml.taska.mapper.NotebookMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.Task;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.TaskRepository;
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
class NotebookServiceTest {

    @Mock
    private NotebookRepository notebookRepository;

    @Mock
    private NotebookMapper notebookMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CacheConfig cacheConfig;

    @InjectMocks
    private NotebookService notebookService;

    private Notebook notebook;
    private CreateNotebookDto createNotebookDto;
    private DisplayNotebookDto displayNotebookDto;
    private Task task;
    private Tag tag;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");

        notebook = new Notebook();
        notebook.setId(1L);
        notebook.setTitle("Test Notebook");
        notebook.setContent("Test Content");
        notebook.setTask(task);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        notebook.setTags(tags);

        createNotebookDto = new CreateNotebookDto();
        createNotebookDto.setTitle("Test Notebook");
        createNotebookDto.setContent("Test Content");
        createNotebookDto.setTaskId(1L);
        createNotebookDto.setTagIds(Set.of(1L));

        displayNotebookDto = new DisplayNotebookDto();
        displayNotebookDto.setId(1L);
        displayNotebookDto.setTitle("Test Notebook");
        displayNotebookDto.setContent("Test Content");
    }

    @Test
    void getAllNotebooksReturnsAllNotebooks() {
        List<Notebook> notebooks = List.of(notebook);
        when(cacheConfig.getAllNotebooks()).thenReturn(null);
        when(notebookRepository.findAll()).thenReturn(notebooks);
        when(notebookMapper.toDisplayNotebookDto(any(Notebook.class))).thenReturn(displayNotebookDto);
        doNothing().when(cacheConfig).putAllNotebooks(anyList());

        List<DisplayNotebookDto> result = notebookService.getAllNotebooks();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayNotebookDto, result.get(0));
        verify(notebookRepository, times(1)).findAll();
        verify(cacheConfig, times(1)).putAllNotebooks(anyList());
    }

    @Test
    void getAllNotebooksReturnsAllNotebooksFromCache() {
        List<DisplayNotebookDto> notebooks = List.of(displayNotebookDto);
        when(cacheConfig.getAllNotebooks()).thenReturn(notebooks);

        List<DisplayNotebookDto> result = notebookService.getAllNotebooks();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayNotebookDto, result.get(0));
        verify(notebookRepository, never()).findAll();
        verify(cacheConfig, never()).putAllNotebooks(anyList());
    }

    @Test
    void getAllNotebooksFullWithCacheReturnsAllNotebooks() {
        List<Notebook> notebooks = List.of(notebook);
        DisplayNotebookFullDto displayNotebookFullDto = new DisplayNotebookFullDto();
        displayNotebookFullDto.setId(1L);
        displayNotebookFullDto.setTitle("Test Notebook");
        displayNotebookFullDto.setContent("Test Content");
        when(cacheConfig.getAllNotebooksFull()).thenReturn(null);
        when(notebookRepository.findAll()).thenReturn(notebooks);
        when(notebookMapper.toDisplayNotebookFullDto(any(Notebook.class))).thenReturn(displayNotebookFullDto);
        doNothing().when(cacheConfig).putAllNotebooksFull(anyList());

        List<DisplayNotebookFullDto> result = notebookService.getAllNotebooksFullWithCache();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayNotebookFullDto, result.get(0));
        verify(notebookRepository, times(1)).findAll();
        verify(cacheConfig, times(1)).putAllNotebooksFull(anyList());
    }

    @Test
    void getAllNotebooksFullWithCacheReturnsAllNotebooksFromCache() {
        DisplayNotebookFullDto displayNotebookFullDto = new DisplayNotebookFullDto();
        displayNotebookFullDto.setId(1L);
        displayNotebookFullDto.setTitle("Test Notebook");
        displayNotebookFullDto.setContent("Test Content");
        List<DisplayNotebookFullDto> notebooks = List.of(displayNotebookFullDto);
        when(cacheConfig.getAllNotebooksFull()).thenReturn(notebooks);

        List<DisplayNotebookFullDto> result = notebookService.getAllNotebooksFullWithCache();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayNotebookFullDto, result.get(0));
        verify(notebookRepository, never()).findAll();
        verify(cacheConfig, never()).putAllNotebooksFull(anyList());
    }

    @Test
    void getAllNotebooksFullReturnsFilteredNotebooks() {
        List<Object[]> results = new ArrayList<>();
        Object[] row = new Object[]{1L, "Test Notebook", "Test Content", 1L, "Test Tag", 1L, "Test Task"};
        results.add(row);
        when(notebookRepository.findAllNotebooksFullWithTagFilter(anyString())).thenReturn(results);

        List<DisplayNotebookFullDto> result = notebookService.getAllNotebooksFull("Test Tag");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getTags());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals("Test Tag", result.get(0).getTags().iterator().next().getName());
        assertEquals("Test Task", result.get(0).getTask().getTitle());
        verify(notebookRepository, times(1)).findAllNotebooksFullWithTagFilter(anyString());
    }

    @Test
    void getAllNotebooksSearchReturnsFilteredNotebooks() {
        List<Notebook> notebooks = List.of(notebook);
        when(notebookRepository.searchByTitle(anyString())).thenReturn(notebooks);
        when(notebookMapper.toDisplayNotebookDto(any(Notebook.class))).thenReturn(displayNotebookDto);

        List<DisplayNotebookDto> result = notebookService.getAllNotebooksSearch("Test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayNotebookDto, result.get(0));
        verify(notebookRepository, times(1)).searchByTitle(anyString());
        verify(notebookRepository, never()).findAll();
    }

    @Test
    void getAllNotebooksSearchReturnsAllNotebooks() {
        List<Notebook> notebooks = List.of(notebook);
        when(notebookRepository.findAll()).thenReturn(notebooks);
        when(notebookMapper.toDisplayNotebookDto(any(Notebook.class))).thenReturn(displayNotebookDto);

        List<DisplayNotebookDto> result = notebookService.getAllNotebooksSearch(null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(displayNotebookDto, result.get(0));
        verify(notebookRepository, times(1)).findAll();
        verify(notebookRepository, never()).searchByTitle(anyString());
    }

    @Test
    void getNotebookByIdExistingIdReturnsNotebook() {
        when(notebookRepository.findById(anyLong())).thenReturn(Optional.of(notebook));
        when(notebookMapper.toDisplayNotebookDto(any(Notebook.class))).thenReturn(displayNotebookDto);

        DisplayNotebookDto result = notebookService.getNotebookById(1L);

        assertNotNull(result);
        assertEquals(displayNotebookDto, result);
        verify(notebookRepository, times(1)).findById(1L);
    }

    @Test
    void getNotebookByIdNonExistingIdThrowsNotFound() {
        when(notebookRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> notebookService.getNotebookById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notebookRepository, times(1)).findById(1L);
    }

    @Test
    void createNotebookValidDtoReturnsCreatedNotebook() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(notebookMapper.fromCreateNotebookDto(any(CreateNotebookDto.class))).thenReturn(notebook);
        when(notebookRepository.save(any(Notebook.class))).thenReturn(notebook);
        when(notebookMapper.toDisplayNotebookDto(any(Notebook.class))).thenReturn(displayNotebookDto);
        doNothing().when(cacheConfig).removeAllNotebooks();
        doNothing().when(cacheConfig).removeAllNotebooksFull();

        DisplayNotebookDto result = notebookService.createNotebook(createNotebookDto);

        assertNotNull(result);
        assertEquals(displayNotebookDto, result);
        verify(notebookRepository, times(1)).save(any(Notebook.class));
        verify(taskRepository, times(1)).findById(anyLong());
        verify(tagRepository, times(1)).findById(anyLong());
        verify(cacheConfig, times(1)).removeAllNotebooks();
        verify(cacheConfig, times(1)).removeAllNotebooksFull();
    }

    @Test
    void createNotebookEmptyTitleThrowsValidationException() {
        createNotebookDto.setTitle("");

        assertThrows(ValidationException.class, () -> notebookService.createNotebook(createNotebookDto));

        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void createNotebookEmptyContentThrowsValidationException() {
        createNotebookDto.setContent("");

        assertThrows(ValidationException.class, () -> notebookService.createNotebook(createNotebookDto));

        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void createNotebookNonExistingTaskThrowsNotFound() {
        createNotebookDto.setTaskId(999L);
        
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> notebookService.createNotebook(createNotebookDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void createNotebookNonExistingTagThrowsNotFound() {
        createNotebookDto.setTagIds(Set.of(999L));
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> notebookService.createNotebook(createNotebookDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void updateNotebookExistingIdReturnsUpdatedNotebook() {
        when(notebookRepository.findById(anyLong())).thenReturn(Optional.of(notebook));
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(notebookRepository.save(any(Notebook.class))).thenReturn(notebook);
        when(notebookMapper.toDisplayNotebookDto(any(Notebook.class))).thenReturn(displayNotebookDto);
        doNothing().when(cacheConfig).removeAllNotebooks();
        doNothing().when(cacheConfig).removeAllNotebooksFull();

        DisplayNotebookDto result = notebookService.updateNotebook(1L, createNotebookDto);

        assertNotNull(result);
        assertEquals(displayNotebookDto, result);
        verify(notebookRepository, times(1)).save(any(Notebook.class));
        verify(tagRepository, times(1)).findById(anyLong());
        verify(cacheConfig, times(1)).removeAllNotebooks();
        verify(cacheConfig, times(1)).removeAllNotebooksFull();
    }

    @Test
    void updateNotebookNonExistingIdThrowsNotFound() {
        when(notebookRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> notebookService.updateNotebook(1L, createNotebookDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void updateNotebookEmptyTitleThrowsValidationException() {
        createNotebookDto.setTitle("");
        
        assertThrows(ValidationException.class, () -> notebookService.updateNotebook(1L, createNotebookDto));

        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void updateNotebookEmptyContentThrowsValidationException() {
        createNotebookDto.setContent("");
        
        assertThrows(ValidationException.class, () -> notebookService.updateNotebook(1L, createNotebookDto));

        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void updateNotebookNonExistingTagThrowsNotFound() {
        createNotebookDto.setTagIds(Set.of(999L));
        when(notebookRepository.findById(anyLong())).thenReturn(Optional.of(notebook));
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> notebookService.updateNotebook(1L, createNotebookDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    void deleteNotebookExistingIdDeletesNotebook() {
        when(notebookRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(notebookRepository).deleteById(anyLong());
        doNothing().when(cacheConfig).removeAllNotebooks();
        doNothing().when(cacheConfig).removeAllNotebooksFull();

        notebookService.deleteNotebook(1L);

        verify(notebookRepository, times(1)).deleteById(1L);
        verify(cacheConfig, times(1)).removeAllNotebooks();
        verify(cacheConfig, times(1)).removeAllNotebooksFull();
    }

    @Test
    void deleteNotebookNonExistingIdThrowsNotFound() {
        when(notebookRepository.existsById(anyLong())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> notebookService.deleteNotebook(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notebookRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void invalidateNotebookCacheRemovesAllCache() {
        doNothing().when(cacheConfig).removeAllNotebooks();
        doNothing().when(cacheConfig).removeAllNotebooksFull();

        notebookService.invalidateNotebookCache();

        verify(cacheConfig, times(1)).removeAllNotebooks();
        verify(cacheConfig, times(1)).removeAllNotebooksFull();
    }
}
