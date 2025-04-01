package com.geml.taska.controllers;

import com.geml.taska.dto.CreateTagDto;
import com.geml.taska.dto.DisplayTagDto;
import com.geml.taska.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tags")
@Tag(name = "Tag", description = "API для управления тегами")
public class TagController {

    private final TagService tagService;


    public TagController(final TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "Получить все теги", description = "Возвращает список всех тегов.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список тегов успешно получен",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = DisplayTagDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<DisplayTagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @Operation(summary = "Получить тег по ID", description = "Возвращает тег по его идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Тег успешно получен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTagDto.class))),
        @ApiResponse(responseCode = "404", description = "Тег не найден", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayTagDto> getTag(
        @Parameter(description = "Идентификатор тега", example = "1") final @PathVariable Long id
    ) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @Operation(summary = "Создать новый тег", description = "Создает новый тег.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Тег успешно создан",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTagDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DisplayTagDto> createTag(
        @Parameter(description = "Данные для создания тега") final @Valid @RequestBody CreateTagDto dto
    ) {
        DisplayTagDto createdTag = tagService.createTag(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @Operation(summary = "Создать несколько тегов", description = "Создает несколько тегов за один запрос.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Теги успешно созданы",
                content = @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = DisplayTagDto.class)))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content)
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<DisplayTagDto>> createTagsBulk(
            @Parameter(description = "Список данных для создания тегов") @Valid @RequestBody List<CreateTagDto> createTagDtos
    ) {
        List<DisplayTagDto> createdTags = tagService.createTags(createTagDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTags);
    }

    @Operation(summary = "Обновить тег", description = "Обновляет существующий тег.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Тег успешно обновлен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayTagDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content),
        @ApiResponse(responseCode = "404", description = "Тег не найден", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayTagDto> updateTag(
        @Parameter(description = "Идентификатор тега для обновления", example = "1") final @PathVariable Long id,
        @Parameter(description = "Данные для обновления тега") final @Valid @RequestBody CreateTagDto dto
    ) {
        return ResponseEntity.ok(tagService.updateTag(id, dto));
    }

    @Operation(summary = "Удалить тег", description = "Удаляет тег по его идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Тег успешно удален", content = @Content),
        @ApiResponse(responseCode = "404", description = "Тег не найден", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
        @Parameter(description = "Идентификатор тега для удаления", example = "1") final @PathVariable Long id
    ) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
