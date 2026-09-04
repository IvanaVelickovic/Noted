package com.Noted.client.dto;

import java.util.List;

public record SummarizeRequest(
        String model,
        List<ChatMessage> messages
) {
    public record ChatMessage(String role, String content) {}
}
