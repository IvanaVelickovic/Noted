package com.Noted.client.dto;

import java.util.List;

public record SummarizeResponse(
        List<Candidate> candidates
) {
    public record Candidate(Content content) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    public String getFirstText(){
        if (candidates == null || candidates.isEmpty()){
            throw new RuntimeException("Empty response from AI api");
        }
        Candidate candidate = candidates.get(0);
        if (candidate.content() == null || candidate.content().parts() == null || candidate.content().parts().isEmpty()){
            throw new RuntimeException("Malformed candidate content from AI api");
        }

        return candidate.content().parts().get(0).text();
    }
}
