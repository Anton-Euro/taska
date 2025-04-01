package com.geml.taska.service;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.mapper.TagMapper;
import com.geml.taska.models.Notebook;
import com.geml.taska.models.Tag;
import com.geml.taska.models.User;
import com.geml.taska.repository.NotebookRepository;
import com.geml.taska.repository.TagRepository;
import com.geml.taska.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final NotebookRepository notebookRepository;
    private final TagMapper tagMapper;
    private final NotebookService notebookService;


    public TagService(
        final TagRepository tagRepository,
        final UserRepository userRepository,
        final NotebookRepository notebookRepository,
        final TagMapper tagMapper,
        final NotebookService notebookService
    ) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.notebookRepository = notebookRepository;
        this.tagMapper = tagMapper;
        this.notebookService = notebookService;
    }


    public List<DisplayTagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toDisplayTagDto)
                .collect(Collectors.toList());
    }


    public DisplayTagDto getTagById(final Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")
        );
        return tagMapper.toDisplayTagDto(tag);
    }


    public DisplayTagDto createTag(final CreateTagDto dto) {
        Tag tag = tagMapper.fromCreateTagDto(dto);
        User user = userRepository.findById(dto.getUserId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
        tag.setUser(user);
        Tag saved = tagRepository.save(tag);
        notebookService.invalidateNotebookCache();
        return tagMapper.toDisplayTagDto(saved);
    }

    public List<DisplayTagDto> createTags(List<CreateTagDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("List of CreateTagDto cannot be null or empty");
        }

        List<Tag> tagsToSave = dtos.stream()
                .map(dto -> {
                    Tag tag = tagMapper.fromCreateTagDto(dto);
                    User user = userRepository.findById(dto.getUserId()).orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for tag: " + dto.getName())
                    );
                    tag.setUser(user);
                    return tag;
                })
                .collect(Collectors.toList());

        List<Tag> savedTags = tagRepository.saveAll(tagsToSave);
        notebookService.invalidateNotebookCache();
        return savedTags.stream()
                .map(tagMapper::toDisplayTagDto)
                .collect(Collectors.toList());
    }


    public DisplayTagDto updateTag(final Long id, final CreateTagDto dto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tag.setName(dto.getName());
        Tag saved = tagRepository.save(tag);
        notebookService.invalidateNotebookCache();
        return tagMapper.toDisplayTagDto(saved);
    }

    @Transactional
    public void deleteTag(final Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")
        );
        Set<Notebook> notebooks = notebookRepository.findByTagsId(id);
        notebooks.forEach(notebook -> {
            notebook.getTags().remove(tag);
            notebookRepository.save(notebook);
        });
        tagRepository.deleteById(id);
        notebookService.invalidateNotebookCache();
    }
}
