package com.Noted.controller;

import com.Noted.model.SummaryJob;
import com.Noted.response.SummaryJobResponse;
import com.Noted.service.SummaryJobService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final SummaryJobService summaryJobService;

    public JobController(SummaryJobService summaryJobService) {
        this.summaryJobService = summaryJobService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SummaryJobResponse> getJob(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                     @PathVariable Long id){
        SummaryJob job = summaryJobService.getSummaryJobById(authHeader.substring(7), id);
        return ResponseEntity.ok(SummaryJobResponse.fromEntity(job));

    }
}
