package com.Noted.client;

import com.Noted.client.dto.SummarizeRequest;
import com.Noted.client.dto.SummarizeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SummarizationClient {
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public SummarizationClient(@Value("${ai.api.key}") String apiKey,
                               @Value("${ai.api.url}") String apiUrl,
                               @Value("${ai.api.model}") String model){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(30000);

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .requestFactory(requestFactory)
                .build();
        this.model = model;
        this.apiKey = apiKey;
    }

    public String summarize(String noteText){
        String prompt = "Summarize the following note in 3-4 concise sentences. If the note is shorter than 3 sentences, summarize it in one sentence:\n\n" + noteText;

        SummarizeRequest requestPayload = new SummarizeRequest(
                model,
                List.of(new SummarizeRequest.ChatMessage("user", prompt))
        );

        SummarizeResponse res = restClient.post()
                .uri("openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestPayload)
                .retrieve()
                .body(SummarizeResponse.class);

        if(res == null){
            throw new RuntimeException("Empty response from Cerebras API");
        }

        return res.getFirstText();
    }
}
