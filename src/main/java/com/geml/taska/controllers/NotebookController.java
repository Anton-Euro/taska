package com.geml.taska.controllers;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.service.NotebookService;
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
 * REST контроллер для управления блокнотами.
 */
@RestController
@RequestMapping("/api/notebooks")
public class NotebookController {

  private final NotebookService notebookService;

  /**
   * Конструктор.
   *
   * @param notebookService сервис блокнотов
   */
  public NotebookController(final NotebookService notebookService) {
    this.notebookService = notebookService;
  }

  /**
   * Получить все блокноты.
   *
   * @return список NotebookDto
   */
  @GetMapping
  public ResponseEntity<List<DisplayNotebookDto>> getAllNotebooks() {
    return ResponseEntity.ok(notebookService.getAllNotebooks());
  }

  /**
   * Получить блокнот по id.
   *
   * @param id идентификатор блокнота
   * @return NotebookDto
   */
  @GetMapping("/{id}")
  public ResponseEntity<DisplayNotebookDto> getNotebook(final @PathVariable Long id) {
    return ResponseEntity.ok(notebookService.getNotebookById(id));
  }

  /**
   * Создать блокнот.
   *
   * @param dto объект NotebookDto
   * @return созданный NotebookDto
   */
  @PostMapping
  public ResponseEntity<DisplayNotebookDto> createNotebook(
      final @RequestBody CreateNotebookDto dto) {
    DisplayNotebookDto createdNotebook = notebookService.createNotebook(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdNotebook);
  }

  /**
   * Обновить блокнот.
   *
   * @param id идентификатор блокнота
   * @param dto объект NotebookDto
   * @return обновленный NotebookDto
   */
  @PutMapping("/{id}")
  public ResponseEntity<DisplayNotebookDto> updateNotebook(final @PathVariable Long id,
      final @RequestBody CreateNotebookDto dto) {
    return ResponseEntity.ok(notebookService.updateNotebook(id, dto));
  }

  /**
   * Удалить блокнот.
   *
   * @param id идентификатор блокнота
   * @return ResponseEntity без содержимого
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotebook(final @PathVariable Long id) {
    notebookService.deleteNotebook(id);
    return ResponseEntity.noContent().build();
  }
}