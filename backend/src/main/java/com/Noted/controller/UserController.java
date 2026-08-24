package com.Noted.controller;

import com.Noted.dto.LoginRequest;
import com.Noted.dto.RegisterRequest;
import com.Noted.model.User;
import com.Noted.response.UserResponse;
import com.Noted.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
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
    public ResponseEntity<UserResponse> authenticateUser(@Valid @RequestBody LoginRequest request){
        User loginUser = userService.authenticateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(UserResponse.fromEntity(loginUser));
    }
}
