package com.Noted.response;

public record CategoryNoteCount(
        Long categoryId,
        String categoryName,
        Long noteCount
) { }
