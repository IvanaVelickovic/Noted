package com.Noted.controller;

import com.Noted.model.SummaryJob;
import com.Noted.response.SummaryJobResponse;
import com.Noted.service.SummaryJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@Tag(name = "Summary jobs", description = "Get summary job by id")
@SecurityRequirement(name = "bearerAuth")
public class JobController {
    private final SummaryJobService summaryJobService;

    public JobController(SummaryJobService summaryJobService) {
        this.summaryJobService = summaryJobService;
    }

    @Operation(summary = "Get summary job by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary job successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Summary job with that id couldn't be found"),

    })
    @GetMapping("/{id}")
    public ResponseEntity<SummaryJobResponse> getJob(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                     @PathVariable Long id){
        SummaryJob job = summaryJobService.getSummaryJobById(authHeader.substring(7), id);
        return ResponseEntity.ok(SummaryJobResponse.fromEntity(job));

    }
}
