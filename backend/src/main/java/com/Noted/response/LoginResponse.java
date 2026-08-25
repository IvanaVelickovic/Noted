package com.Noted.response;

public record LoginResponse(
        String accessToken, String refreshToken, UserResponse userResponse
) { }
