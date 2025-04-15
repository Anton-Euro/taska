package com.geml.taska.controllers;

import com.geml.taska.dto.LogCreationStatusDto;
import com.geml.taska.models.LogFile;
import com.geml.taska.service.AsyncLogService;
import com.geml.taska.service.AsyncLogService.LogStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log", description = "API для управления логами")
public class LogController {

    private static final String LOG_DIRECTORY = "logs/";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    private final AsyncLogService asyncLogService;

    public LogController(AsyncLogService asyncLogService) {
        this.asyncLogService = asyncLogService;
    }

    @Operation(summary = "Создать лог файл асинхронно", description = "Создает лог файл асинхронно и возвращает ID задачи.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Задача на создание лог файла принята",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", format = "UUID"))),
        @ApiResponse(responseCode = "500", description = "Ошибка при создании задачи", content = @Content)
    })
    @PostMapping("/{date}")
    public CompletableFuture<ResponseEntity<String>> createLogFileAsync(
        @Parameter(description = "Дата лога в формате YYYY-MM-DD", example = "2023-10-27") @PathVariable String date
    ) {
        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return asyncLogService.createLogFileAsync(logDate)
                .thenApply(logId -> ResponseEntity.status(HttpStatus.ACCEPTED).body(logId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating log file task", e);
        }
    }

    @Operation(summary = "Получить статус создания лог файла", description = "Возвращает статус создания лог файла по ID задачи.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно получен",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogCreationStatusDto.class))),
        @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content)
    })
    @GetMapping("/status/{logId}")
    public ResponseEntity<LogCreationStatusDto> getLogCreationStatus(
        @Parameter(description = "ID задачи на создание лог файла", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable UUID logId
    ) {
        LogCreationStatusDto status = asyncLogService.getLogCreationStatus(logId);
        if (status.getStatus() == LogStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log creation task not found");
        }
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Получить лог файл по дате и ротации", description = "Возвращает лог файл за указанную дату и номер ротации.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лог файл успешно получен",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "404", description = "Лог файл не найден", content = @Content),
        @ApiResponse(responseCode = "500", description = "Ошибка при чтении лог файла", content = @Content)
    })
    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogFileByDate(
        @Parameter(description = "Дата лога в формате YYYY-MM-DD", example = "2023-10-27") @PathVariable String date,
        @Parameter(description = "Номер ротации лога", example = "0") @RequestParam Integer rotation
    ) {
        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            String logFileName;
            Path logFilePath;


            logFileName = "app-"
                + logDate.format(DATE_FORMATTER)
                + "." + rotation + ".log";
            logFilePath = Paths.get(LOG_DIRECTORY, logFileName);

            if (!Files.exists(logFilePath)) {
                String reason = "Log file not found for date: " + date;
                if (rotation != null) {
                    reason += " and rotation: " + rotation;
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
            }

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(logFilePath));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + logFileName);
            headers.setContentType(MediaType.TEXT_PLAIN);

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(Files.size(logFilePath))
                .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error reading log file", e
            );
        }
    }

    @Operation(summary = "Получить все лог файлы за дату", description = "Возвращает все лог файлы за указанную дату, включая все ротации.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лог файлы успешно получены",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "404", description = "Лог файлы не найдены", content = @Content),
        @ApiResponse(responseCode = "500", description = "Ошибка при чтении лог файлов", content = @Content)
    })
    @GetMapping("/all/{date}")
    public ResponseEntity<Resource> getAllLogFileByDate(
        @Parameter(description = "Дата логов в формате YYYY-MM-DD", example = "2023-10-27") @PathVariable String date
    ) {
        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            String logFileNamePattern = "app-"
                + logDate.format(DATE_FORMATTER) + ".*.log";
            Path logDirectoryPath = Paths.get(LOG_DIRECTORY);

            if (!Files.exists(logDirectoryPath) || !Files.isDirectory(logDirectoryPath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log directory not found");
            }

            java.util.List<Path> matchingFiles = Files.list(logDirectoryPath)
                .filter(path -> path.getFileName().toString()
                    .matches(logFileNamePattern.replace(".", "\\.")
                        .replace("*", ".*")))
                .toList();

            if (matchingFiles.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Log files not found for date: " + date
                );
            }

            StringBuilder combinedLogContent = new StringBuilder();
            for (Path logFilePath : matchingFiles) {
                combinedLogContent.append(new String(Files.readAllBytes(logFilePath))).append("\n");
            }

            ByteArrayResource resource = new ByteArrayResource(
                combinedLogContent.toString().getBytes()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=app-"
                    + logDate.format(DATE_FORMATTER) + ".log"
            );
            headers.setContentType(MediaType.TEXT_PLAIN);

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(combinedLogContent.length())
                .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error reading log files", e
            );
        }
    }

    @Operation(summary = "Получить готовый лог файл по ID задачи", description = "Возвращает готовый лог файл по ID задачи.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лог файл успешно получен",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "404", description = "Задача не найдена или лог файл не готов", content = @Content),
        @ApiResponse(responseCode = "500", description = "Ошибка при чтении лог файла", content = @Content)
    })
    @GetMapping("/file/{logId}")
    public ResponseEntity<Resource> getLogFileByLogId(
        @Parameter(description = "ID задачи на создание лог файла", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable UUID logId
    ) {
        LogFile logFile = asyncLogService.getLogFile(logId);
        if (logFile == null || logFile.getStatus() != LogStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log file not ready or task not found");
        }

        String filePath = logFile.getFilePath();
        if (filePath == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File path not available");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log file not found");
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(file.length())
            .body(resource);
    }
}
