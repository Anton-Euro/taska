package com.geml.taska.dto;

import com.geml.taska.service.AsyncLogService.LogStatus;

public class LogCreationStatusDto {
    private final LogStatus status;
    private final String errorMessage;

    public LogCreationStatusDto(LogStatus status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public LogStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
