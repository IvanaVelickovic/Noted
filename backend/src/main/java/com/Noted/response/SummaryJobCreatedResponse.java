package com.Noted.response;

import com.Noted.model.SummaryJob;

public record SummaryJobCreatedResponse(
        Long jobId,
        String status
) {
    public static SummaryJobCreatedResponse fromEntity(SummaryJob job){
        return new SummaryJobCreatedResponse(job.getId(), job.getStatus().name());
    }
}
