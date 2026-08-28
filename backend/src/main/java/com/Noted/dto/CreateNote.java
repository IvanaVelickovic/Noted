package com.Noted.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateNote(
        @NotBlank String title,
        @NotBlank String body
) { }
