package com.Noted.controller;

import com.Noted.dto.LoginRequest;
import com.Noted.dto.RefreshRequest;
import com.Noted.dto.RegisterRequest;
import com.Noted.model.RefreshToken;
import com.Noted.model.User;
import com.Noted.response.LoginResponse;
import com.Noted.response.UserResponse;
import com.Noted.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request){
        User savedUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromEntity(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest request){
        LoginResponse loginResponse = userService.authenticateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request){
        String newAccessToken = userService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
