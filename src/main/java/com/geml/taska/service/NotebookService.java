package com.geml.taska.service;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import com.geml.taska.mapper.NotebookMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.TaskItem;
import com.geml.taska.models.User;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.TaskItemRepository;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final NotebookMapper notebookMapper;
    private final UserRepository userRepository;
    private final TaskItemRepository taskItemRepository;
    private final TagRepository tagRepository;


    public NotebookService(final NotebookRepository notebookRepository,
            final NotebookMapper notebookMapper,
            final UserRepository userRepository,
            final TaskItemRepository taskItemRepository,
            final TagRepository tagRepository) {
        this.notebookRepository = notebookRepository;
        this.notebookMapper = notebookMapper;
        this.userRepository = userRepository;
        this.taskItemRepository = taskItemRepository;
        this.tagRepository = tagRepository;
    }


    public List<DisplayNotebookDto> getAllNotebooks(final String title) {
        List<Notebook> notebooks = (title != null && !title.isEmpty())
                ? notebookRepository.searchByTitle(title)
                : notebookRepository.findAll();
        return notebooks.stream()
                .map(notebookMapper::toDisplayNotebookDto).toList();
    }

    public List<DisplayNotebookFullDto> getAllNotebooksFull(final String title) {
        List<Notebook> notebooks = (title != null && !title.isEmpty())
                ? notebookRepository.searchByTitle(title)
                : notebookRepository.findAll();
        return notebooks.stream()
                .map(notebookMapper::toDisplayNotebookFullDto).toList();
    }


    public DisplayNotebookDto getNotebookById(final Long id) {
        Notebook nb = notebookRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notebook not found")
        );
        return notebookMapper.toDisplayNotebookDto(nb);
    }


    public DisplayNotebookDto createNotebook(final CreateNotebookDto dto) {
        Notebook nb = notebookMapper.fromCreateNotebookDto(dto);
        User user = userRepository.findById(dto.getUserId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
        nb.setUser(user);
        if (dto.getTaskItemId() != null) {
            TaskItem taskItem = taskItemRepository.findById(dto.getTaskItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task item not found")
            );
            nb.setTaskItem(taskItem);
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
        return notebookMapper.toDisplayNotebookDto(saved);
    }


    public DisplayNotebookDto updateNotebook(final Long id, final CreateNotebookDto dto) {
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
        return notebookMapper.toDisplayNotebookDto(saved);
    }


    public void deleteNotebook(final Long id) {
        if (!notebookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notebook not found");
        }
        notebookRepository.deleteById(id);
    }
}