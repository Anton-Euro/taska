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

/**
 * REST-контроллер для управления тегами.
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

  private final TagService tagService;

  /**
   * Конструктор.
   *
   * @param tagService сервис тегов
   */
  public TagController(final TagService tagService) {
    this.tagService = tagService;
  }

  /**
   * Получить все теги.
   *
   * @return список TagDto
   */
  @GetMapping
  public ResponseEntity<List<DisplayTagDto>> getAllTags() {
    return ResponseEntity.ok(tagService.getAllTags());
  }

  /**
   * Получить тег по id.
   *
   * @param id идентификатор тега
   * @return ResponseEntity с TagDto или 404, если тег не найден
   */
  @GetMapping("/{id}")
  public ResponseEntity<DisplayTagDto> getTag(final @PathVariable Long id) {
    return ResponseEntity.ok(tagService.getTagById(id));
  }

  /**
   * Создать тег.
   *
   * @param dto объект TagDto
   * @return созданный TagDto с кодом 201 (Created)
   */
  @PostMapping
  public ResponseEntity<DisplayTagDto> createTag(final @RequestBody CreateTagDto dto) {
    DisplayTagDto createdTag = tagService.createTag(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
  }

  /**
   * Обновить тег.
   *
   * @param id идентификатор тега
   * @param dto объект TagDto
   * @return обновленный TagDto
   */
  @PutMapping("/{id}")
  public ResponseEntity<DisplayTagDto> updateTag(final @PathVariable Long id,
      final @RequestBody CreateTagDto dto) {
    return ResponseEntity.ok(tagService.updateTag(id, dto));
  }

  /**
   * Удалить тег.
   *
   * @param id идентификатор тега
   * @return ResponseEntity с кодом 204 (No Content) или 404, если тег не найден
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(final @PathVariable Long id) {
    tagService.deleteTag(id);
    return ResponseEntity.noContent().build();
  }
}