package com.Noted.client.dto;

import java.util.List;

public record SummarizeResponse(
        List<Choice> choices
) {
    public record Choice(Message message) {}
    public record Message(String content) {}

    public String getFirstText(){
        if(choices == null || choices.isEmpty()){
            throw new RuntimeException("Empty response from AI api");
        }
        return choices.get(0).message().content();
    }
}
