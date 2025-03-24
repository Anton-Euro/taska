package com.geml.taska.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private static final String LOG_DIRECTORY = "logs/";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogFileByDate(
            @PathVariable String date,
            @RequestParam Integer rotation
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

    @GetMapping("/all/{date}")
    public ResponseEntity<Resource> getAllLogFileByDate(@PathVariable String date) {
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
}
