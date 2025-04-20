package com.geml.taska.service;

import com.geml.taska.config.CacheConfig;
import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.dto.DisplayTaskDto;
import com.geml.taska.exception.ValidationException;
import com.geml.taska.mapper.NotebookMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.Task;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.TaskRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@Slf4j
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final NotebookMapper notebookMapper;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final CacheConfig cacheConfig;


    public NotebookService(
        final NotebookRepository notebookRepository,
        final NotebookMapper notebookMapper,
        final TaskRepository taskRepository,
        final TagRepository tagRepository,
        final CacheConfig cacheConfig
    ) {
        this.notebookRepository = notebookRepository;
        this.notebookMapper = notebookMapper;
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.cacheConfig = cacheConfig;
    }


    public List<DisplayNotebookDto> getAllNotebooks(Long taskId) {
        List<DisplayNotebookDto> cachedNotebooks = cacheConfig.getAllNotebooks();
        if (cachedNotebooks != null && taskId == null) {
            log.debug("Getting all notebooks from cache");
            return cachedNotebooks;
        }

        log.debug("Getting all notebooks from database");
        List<Notebook> notebooks;
        if (taskId != null) {
            notebooks = notebookRepository.findByTaskIdFilter(taskId);
        } else {
            notebooks = notebookRepository.findAll();
        }
        List<DisplayNotebookDto> displayNotebooks = notebooks.stream()
                .map(notebookMapper::toDisplayNotebookDto).toList();
        if (taskId == null) {
            cacheConfig.putAllNotebooks(displayNotebooks);
        }
        return displayNotebooks;
    }

    public List<DisplayNotebookFullDto> getAllNotebooksFull(Long taskId) {
        List<Object[]> results;
        results = notebookRepository.findAllNotebooksFullWithTagAndTaskFilter(taskId);
        return processResults(results);
    }
    
    

    public List<DisplayNotebookFullDto> getAllNotebooksFull(String tagName) {
        List<Object[]> results = notebookRepository.findAllNotebooksFullWithTagFilter(tagName);
        return processResults(results);
    }

    @SuppressWarnings("unused")
    private List<DisplayNotebookFullDto> processResults(List<Object[]> results) {
        Map<Long, DisplayNotebookFullDto> notebookMap = new HashMap<>();

        for (Object[] row : results) {
            Long notebookId = ((Number) row[0]).longValue();
            String notebookTitle = (String) row[1];
            String notebookContent = (String) row[2];
            Long tagId = row[3] != null ? ((Number) row[3]).longValue() : null;
            String tagName = (String) row[4];
            Long taskId = row[5] != null ? ((Number) row[5]).longValue() : null;
            String taskTitle = (String) row[6];

            notebookMap.computeIfAbsent(notebookId, id -> {
                DisplayNotebookFullDto dto = new DisplayNotebookFullDto();
                dto.setId(notebookId);
                dto.setTitle(notebookTitle);
                dto.setContent(notebookContent);
                dto.setTags(new HashSet<>());
                dto.setTask(null);
                return dto;
            });

            DisplayNotebookFullDto notebookDto = notebookMap.get(notebookId);
            if (tagId != null) {
                DisplayTagDto tagDto = new DisplayTagDto();
                tagDto.setId(tagId);
                tagDto.setName(tagName);
                notebookDto.getTags().add(tagDto);
            }
            if (taskId != null) {
                DisplayTaskDto taskDto = new DisplayTaskDto();
                taskDto.setId(taskId);
                taskDto.setTitle(taskTitle);
                notebookDto.setTask(taskDto);
            }
        }

        return new ArrayList<>(notebookMap.values());
    }

    public List<DisplayNotebookDto> getAllNotebooksSearch(final String title) {
        List<Notebook> notebooks = (title != null && !title.isEmpty())
                ? notebookRepository.searchByTitle(title)
                : notebookRepository.findAll();
        return notebooks.stream()
                .map(notebookMapper::toDisplayNotebookDto).toList();
    }


    public DisplayNotebookDto getNotebookById(final Long id) {
        Notebook nb = notebookRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notebook not found")
        );
        return notebookMapper.toDisplayNotebookDto(nb);
    }


    public DisplayNotebookDto createNotebook(final CreateNotebookDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            errors.add("Title cannot be empty");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            errors.add("Content cannot be empty");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Notebook nb = notebookMapper.fromCreateNotebookDto(dto);
        if (dto.getTaskId() != null) {
            Task taskItem = taskRepository.findById(dto.getTaskId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task item not found")
            );
            nb.setTask(taskItem);
        }
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<Tag> tags = dto.getTagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                    .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"))
                    ).collect(Collectors.toSet());
            nb.setTags(tags);
        }
        Notebook saved = notebookRepository.save(nb);
        invalidateNotebookCache();
        return notebookMapper.toDisplayNotebookDto(saved);
    }


    public DisplayNotebookDto updateNotebook(final Long id, final CreateNotebookDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            errors.add("Title cannot be empty");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            errors.add("Content cannot be empty");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
        Notebook nb = notebookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        nb.setTitle(dto.getTitle());
        nb.setContent(dto.getContent());
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<Tag> tags = dto.getTagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(
                                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .collect(Collectors.toSet());
            nb.setTags(tags);
        }
        Notebook saved = notebookRepository.save(nb);
        invalidateNotebookCache();
        return notebookMapper.toDisplayNotebookDto(saved);
    }

    @Transactional
    public void deleteNotebook(final Long id) {
        if (!notebookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notebook not found");
        }
        notebookRepository.deleteById(id);
        invalidateNotebookCache();
    }
    
    public void invalidateNotebookCache() {
        log.debug("Invalidating notebook cache");
        cacheConfig.removeAllNotebooks();
        cacheConfig.removeAllNotebooksFull();
    }
}
