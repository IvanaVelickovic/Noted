package com.Noted.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Me", description = "Route to check if the authorization passed")
@SecurityRequirement(name = "bearerAuth")
public class MeController {

    @Operation(summary = "Me route")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passed autorization"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
    })
    @GetMapping("/me")
    public ResponseEntity<String> me(){

        return ResponseEntity.ok("We passed authorization!!!!");
    }
}
