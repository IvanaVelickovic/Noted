package com.Noted.client.dto;

import java.util.List;

public record SummarizeRequest(
        List<Content> contents
) {
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    public static SummarizeRequest fromPrompt(String prompt){
        return new SummarizeRequest(
                List.of(new Content(
                        List.of(new Part(prompt))
                ))
        );
    }
}
