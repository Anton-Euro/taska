package com.geml.taska.service;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.mapper.TagMapper;
import com.geml.taska.models.Tag;
import com.geml.taska.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;


    public TagService(final TagRepository tagRepository,
            final TagMapper tagMapper) {
        this.tagRepository = tagRepository;
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


    public void deleteTag(final Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tagRepository.deleteById(id);
    }
}