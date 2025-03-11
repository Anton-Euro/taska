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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final NotebookRepository notebookRepository;
    private final TagMapper tagMapper;


    public TagService(
        final TagRepository tagRepository,
        final UserRepository userRepository,
        final NotebookRepository notebookRepository,
        final TagMapper tagMapper
    ) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.notebookRepository = notebookRepository;
        this.tagMapper = tagMapper;
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
        return tagMapper.toDisplayTagDto(saved);
    }


    public DisplayTagDto updateTag(final Long id, final CreateTagDto dto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tag.setName(dto.getName());
        Tag saved = tagRepository.save(tag);
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
    }
}