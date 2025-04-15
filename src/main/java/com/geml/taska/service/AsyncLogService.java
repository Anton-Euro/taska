package com.geml.taska.service;

import com.geml.taska.dto.LogCreationStatusDto;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AsyncLogService {

    private static final String LOG_DIRECTORY = "logs/";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust thread pool size as needed
    private final Map<UUID, LogCreationStatus> logCreationStatuses = new ConcurrentHashMap<>();

    public UUID createLogFileAsync(LocalDate date) {
        UUID logId = UUID.randomUUID();
        logCreationStatuses.put(logId, new LogCreationStatus(LogStatus.IN_PROGRESS));

        executorService.submit(() -> {
            try {
                Thread.sleep(15000);

                String logFileNamePattern = "app-" + date.format(DATE_FORMATTER) + ".*.log";
                Path logDirectoryPath = Paths.get(LOG_DIRECTORY);

                if (!Files.exists(logDirectoryPath) || !Files.isDirectory(logDirectoryPath)) {
                    logCreationStatuses.put(logId, new LogCreationStatus(LogStatus.FAILED, "Log directory not found"));
                    return;
                }

                List<Path> matchingFiles = Files.list(logDirectoryPath)
                    .filter(path -> path.getFileName().toString()
                        .matches(logFileNamePattern.replace(".", "\\.")
                            .replace("*", ".*")))
                    .collect(Collectors.toList());

                if (matchingFiles.isEmpty()) {
                    logCreationStatuses.put(logId, new LogCreationStatus(LogStatus.FAILED, "Log files not found for date: " + date));
                    return;
                }

                String combinedLogFileName = "app-" + date.format(DATE_FORMATTER) + "-" + logId + ".log";
                Path combinedLogFilePath = Paths.get(LOG_DIRECTORY, combinedLogFileName);
                Files.createDirectories(combinedLogFilePath.getParent());

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(combinedLogFilePath.toFile()))) {
                    for (Path logFilePath : matchingFiles) {
                        List<String> lines = Files.readAllLines(logFilePath);
                        for (String line : lines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }

                logCreationStatuses.put(logId, new LogCreationStatus(LogStatus.COMPLETED, null, combinedLogFilePath.toString()));
            } catch (IOException | InterruptedException e) {
                logCreationStatuses.put(logId, new LogCreationStatus(LogStatus.FAILED, e.getMessage()));
            }
        });

        return logId;
    }

    public LogCreationStatusDto getLogCreationStatus(UUID logId) {
        LogCreationStatus status = logCreationStatuses.getOrDefault(logId, new LogCreationStatus(LogStatus.NOT_FOUND));
        return new LogCreationStatusDto(status.getStatus(), status.getErrorMessage());
    }

    public LogCreationStatus getLogCreationStatusFull(UUID logId) {
        return logCreationStatuses.getOrDefault(logId, new LogCreationStatus(LogStatus.NOT_FOUND));
    }

    public enum LogStatus {
        IN_PROGRESS, COMPLETED, FAILED, NOT_FOUND
    }

    public static class LogCreationStatus {
        private final LogStatus status;
        private final String errorMessage;
        private final String filePath;

        public LogCreationStatus(LogStatus status) {
            this.status = status;
            this.errorMessage = null;
            this.filePath = null;
        }

        public LogCreationStatus(LogStatus status, String errorMessage) {
            this.status = status;
            this.errorMessage = errorMessage;
            this.filePath = null;
        }

        public LogCreationStatus(LogStatus status, String errorMessage, String filePath) {
            this.status = status;
            this.errorMessage = errorMessage;
            this.filePath = filePath;
        }

        public LogStatus getStatus() {
            return status;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
