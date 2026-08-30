package com.Noted.dto;

public record CreateNote(
        String title,
        String body,
        Long categoryId
) { }
