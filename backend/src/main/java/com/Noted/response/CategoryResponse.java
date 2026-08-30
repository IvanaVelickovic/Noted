package com.Noted.response;

import com.Noted.model.Category;

public record CategoryResponse(
        Long id,
        String name
) {
    public static CategoryResponse fromEntity(Category category){
        return new CategoryResponse(category.getId(), category.getName());
    }
}
