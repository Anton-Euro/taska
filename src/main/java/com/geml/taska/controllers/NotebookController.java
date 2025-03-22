package com.geml.taska.controllers;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import com.geml.taska.service.NotebookService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/notebooks")
public class NotebookController {

    private final NotebookService notebookService;


    public NotebookController(final NotebookService notebookService) {
        this.notebookService = notebookService;
    }


    @GetMapping
    public ResponseEntity<List<DisplayNotebookDto>> getAllNotebooks() {
        return ResponseEntity.ok(notebookService.getAllNotebooks());
    }


    @GetMapping("/all")
    public ResponseEntity<List<DisplayNotebookFullDto>> getAllNotebooksFull() {
        return ResponseEntity.ok(notebookService.getAllNotebooksFull());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DisplayNotebookDto>> getAllNotebooksSearch(
        @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(notebookService.getAllNotebooksSearch(title));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisplayNotebookDto> getNotebook(final @PathVariable Long id) {
        return ResponseEntity.ok(notebookService.getNotebookById(id));
    }


    @PostMapping
    public ResponseEntity<DisplayNotebookDto> createNotebook(
        final @Valid @RequestBody CreateNotebookDto dto
    ) {
        DisplayNotebookDto createdNotebook = notebookService.createNotebook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotebook);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisplayNotebookDto> updateNotebook(
        final @PathVariable Long id,
        final @Valid @RequestBody CreateNotebookDto dto
    ) {
        return ResponseEntity.ok(notebookService.updateNotebook(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebook(final @PathVariable Long id) {
        notebookService.deleteNotebook(id);
        return ResponseEntity.noContent().build();
    }
}
