package com.Noted.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(
        @NotBlank String name
) { }
