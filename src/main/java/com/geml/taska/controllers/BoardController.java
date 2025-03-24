package com.geml.taska.controllers;

import com.geml.taska.dto.CreateBoardDto;
import com.geml.taska.dto.DisplayBoardDto;
import com.geml.taska.service.BoardService;
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
@RequestMapping("/api/boards")
@Tag(name = "Board", description = "API для управления досками")
public class BoardController {

    private final BoardService boardService;


    public BoardController(final BoardService boardService) {
        this.boardService = boardService;
    }

    @Operation(summary = "Получить все доски", description = "Возвращает список всех досок. Можно фильтровать по заголовку.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список досок успешно получен",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayBoardDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<DisplayBoardDto>> getAllBoards(
        @Parameter(description = "Заголовок для фильтрации", example = "My Board") @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(boardService.getAllBoards(title));
    }

    @Operation(summary = "Получить доску по ID", description = "Возвращает доску по ее идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Доска успешно получена",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayBoardDto.class))),
        @ApiResponse(responseCode = "404", description = "Доска не найдена", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisplayBoardDto> getBoard(
        @Parameter(description = "Идентификатор доски", example = "1") @PathVariable Long id
    ) {
        DisplayBoardDto boardDto = boardService.getBoardById(id);
        return ResponseEntity.ok(boardDto);
    }

    @Operation(summary = "Создать новую доску", description = "Создает новую доску.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Доска успешно создана",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayBoardDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DisplayBoardDto> createBoard(
        @Parameter(description = "Данные для создания доски") @Valid @RequestBody CreateBoardDto boardDto
    ) {
        DisplayBoardDto createdBoard = boardService.createBoard(boardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
    }

    @Operation(summary = "Обновить доску", description = "Обновляет существующую доску.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Доска успешно обновлена",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DisplayBoardDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные", content = @Content),
        @ApiResponse(responseCode = "404", description = "Доска не найдена", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisplayBoardDto> updateBoard(
        @Parameter(description = "Идентификатор доски для обновления", example = "1") @PathVariable Long id,
        @Parameter(description = "Данные для обновления доски") @Valid @RequestBody CreateBoardDto boardDto
    ) {
        DisplayBoardDto updatedBoard = boardService.updateBoard(id, boardDto);
        return ResponseEntity.ok(updatedBoard);
    }

    @Operation(summary = "Удалить доску", description = "Удаляет доску по ее идентификатору.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Доска успешно удалена", content = @Content),
        @ApiResponse(responseCode = "404", description = "Доска не найдена", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
        @Parameter(description = "Идентификатор доски для удаления", example = "1") @PathVariable Long id
    ) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
