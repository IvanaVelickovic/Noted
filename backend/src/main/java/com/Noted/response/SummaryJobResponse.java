package com.Noted.response;

import com.Noted.model.SummaryJob;

import java.time.LocalDateTime;

public record SummaryJobResponse(
        Long id,
        String status,
        String result,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SummaryJobResponse fromEntity(SummaryJob job){
        return new SummaryJobResponse(job.getId(), job.getStatus().name(), job.getResult(), job.getErrorMessage(), job.getCreatedAt(), job.getUpdatedAt());
    }
}
