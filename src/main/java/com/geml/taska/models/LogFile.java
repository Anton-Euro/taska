package com.geml.taska.models;

import com.geml.taska.service.AsyncLogService.LogStatus;
import lombok.Data;

@Data
public class LogFile {
    private String id;
    private LogStatus status;
    private String filePath;
    private String errorMessage;

    public LogFile(String id) {
        this.id = id;
        this.status = LogStatus.IN_PROGRESS;
    }
}
