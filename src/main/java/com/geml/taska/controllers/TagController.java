package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.service.TagService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;


    public TagController(final TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    public ResponseEntity<List<DisplayTagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisplayTagDto> getTag(final @PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }


    @PostMapping
    public ResponseEntity<DisplayTagDto> createTag(final @RequestBody CreateTagDto dto) {
        DisplayTagDto createdTag = tagService.createTag(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisplayTagDto> updateTag(final @PathVariable Long id,
            final @RequestBody CreateTagDto dto) {
        return ResponseEntity.ok(tagService.updateTag(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(final @PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}