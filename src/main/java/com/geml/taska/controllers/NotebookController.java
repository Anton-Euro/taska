package com.geml.taska.controllers;

import com.geml.taska.dto.CreateNotebookDto;
import com.geml.taska.dto.DisplayNotebookDto;
import com.geml.taska.dto.DisplayNotebookFullDto;
import com.geml.taska.service.NotebookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notebook", description = "Notebook management API")
public class NotebookController {

    private final NotebookService notebookService;


    public NotebookController(final NotebookService notebookService) {
        this.notebookService = notebookService;
    }


    @Operation(summary = "Get all notebooks", description = "Retrieve a list of all notebooks. Optionally filter by taskId.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<DisplayNotebookDto>> getAllNotebooks(
        @Parameter(description = "Task ID to filter by", example = "1") @RequestParam(required = false) Long taskId
    ) {
        return ResponseEntity.ok(notebookService.getAllNotebooks(taskId));
    }


    @Operation(summary = "Get all notebooks with full details", description = "Retrieve a list of all notebooks with full details (tags and task).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookFullDto.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<DisplayNotebookFullDto>> getAllNotebooksFull(
        @Parameter(description = "Task ID to filter by", example = "1") @RequestParam(required = false) Long taskId
    ) {
        return ResponseEntity.ok(notebookService.getAllNotebooksFull(taskId));
    }

    @Operation(summary = "Get all notebooks with full details by tag", 
        description = "Retrieve a list of all notebooks with full details (tags and task) filtered by tag name.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookFullDto.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    })
    @GetMapping("/all/search")
    public ResponseEntity<List<DisplayNotebookFullDto>> getAllNotebooksFullSearch(
        @Parameter(description = "Tag name to filter by", example = "important") @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(notebookService.getAllNotebooksFull(tag));
    }

    @Operation(summary = "Search notebooks by title", description = "Retrieve a list of notebooks matching the given title.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookDto.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<DisplayNotebookDto>> getAllNotebooksSearch(
        @Parameter(description = "Title to search by", example = "My Notebook") @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(notebookService.getAllNotebooksSearch(title));
    }


    @Operation(summary = "Get notebook by ID", description = "Retrieve a notebook by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved notebook",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookDto.class))),
        @ApiResponse(responseCode = "404", description = "Notebook not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayNotebookDto> getNotebook(
        @Parameter(description = "ID of the notebook to retrieve", example = "1") final @PathVariable Long id
    ) {
        return ResponseEntity.ok(notebookService.getNotebookById(id));
    }


    @Operation(summary = "Create a new notebook", description = "Create a new notebook.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created notebook",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DisplayNotebookDto> createNotebook(
        @Parameter(description = "Notebook data to create") final @Valid @RequestBody CreateNotebookDto dto
    ) {
        DisplayNotebookDto createdNotebook = notebookService.createNotebook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotebook);
    }


    @Operation(summary = "Update a notebook", description = "Update an existing notebook.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated notebook",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayNotebookDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notebook not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayNotebookDto> updateNotebook(
        @Parameter(description = "ID of the notebook to update", example = "1") final @PathVariable Long id,
        @Parameter(description = "Notebook data to update") final @Valid @RequestBody CreateNotebookDto dto
    ) {
        return ResponseEntity.ok(notebookService.updateNotebook(id, dto));
    }


    @Operation(summary = "Delete a notebook", description = "Delete a notebook by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted notebook", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notebook not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebook(
        @Parameter(description = "ID of the notebook to delete", example = "1") final @PathVariable Long id
    ) {
        notebookService.deleteNotebook(id);
        return ResponseEntity.noContent().build();
    }
}
