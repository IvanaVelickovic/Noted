package com.Noted.controller;

import com.Noted.dto.LoginRequest;
import com.Noted.dto.RefreshRequest;
import com.Noted.dto.RegisterRequest;
import com.Noted.model.User;
import com.Noted.response.LoginResponse;
import com.Noted.response.UserResponse;
import com.Noted.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/auth")
@RestController
@Tag(name = "User", description = "User authentication (register, login), refresh and logout")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @Operation(summary = "Register user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "New user created"),
            @ApiResponse(responseCode = "400", description = "Bad request - email or password doesn't satisfy safety regulations"),
            @ApiResponse(responseCode = "409", description = "Conflict - User with that email already exists"),
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request){
        User savedUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromEntity(savedUser));
    }

    @Operation(summary = "Login user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login failed"),
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest request){
        LoginResponse loginResponse = userService.authenticateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @Operation(summary = "Get a new access token by using the refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a new access token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Getting a new access token failed"),
    })
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request){
        String newAccessToken = userService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @Operation(summary = "Logout user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content - User successfully logged out"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Logout failed"),
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request){
        userService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
