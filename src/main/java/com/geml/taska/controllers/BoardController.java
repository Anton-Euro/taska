package com.geml.taska.controllers;

import com.geml.taska.dto.CreateBoardDto;
import com.geml.taska.dto.DisplayBoardDto;
import com.geml.taska.service.BoardService;
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
public class BoardController {

    private final BoardService boardService;


    public BoardController(final BoardService boardService) {
        this.boardService = boardService;
    }


    @GetMapping
    public ResponseEntity<List<DisplayBoardDto>> getAllBoards(
        @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(boardService.getAllBoards(title));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisplayBoardDto> getBoard(@PathVariable Long id) {
        DisplayBoardDto boardDto = boardService.getBoardById(id);
        return ResponseEntity.ok(boardDto);
    }


    @PostMapping
    public ResponseEntity<DisplayBoardDto> createBoard(
        @Valid @RequestBody CreateBoardDto boardDto
    ) {
        DisplayBoardDto createdBoard = boardService.createBoard(boardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisplayBoardDto> updateBoard(
        @PathVariable Long id,
        @Valid @RequestBody CreateBoardDto boardDto
    ) {
        DisplayBoardDto updatedBoard = boardService.updateBoard(id, boardDto);
        return ResponseEntity.ok(updatedBoard);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
