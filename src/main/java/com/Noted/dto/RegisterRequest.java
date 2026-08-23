package com.Noted.dto;

public record RegisterRequest(
    String email,
    String name,
    String password
) {}
