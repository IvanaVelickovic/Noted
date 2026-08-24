package com.Noted.response;

import com.Noted.model.User;

public record UserResponse(Long id, String email, String name) {
    public static UserResponse fromEntity(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
