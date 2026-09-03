package com.Noted.response;

import com.Noted.model.SummaryJob;

public record SummaryJobResponse(
        Long id,
        String status,
        String result,
        String error_message
) {
    public static SummaryJobResponse fromEntity(SummaryJob job){
        return new SummaryJobResponse(job.getId(), job.getStatus().name(), job.getResult(), job.getErrorMessage());
    }
}
