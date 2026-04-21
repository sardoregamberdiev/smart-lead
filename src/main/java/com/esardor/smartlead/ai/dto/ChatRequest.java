package com.esardor.smartlead.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatRequest(
        String model,
        List<Message> messages,
        boolean stream,

        @JsonProperty("max_tokens")
        int maxTokens,

        double temperature
) {

    public static ChatRequest of(String model, String systemPrompt, String userMessage) {
        return new ChatRequest(
                model,
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userMessage)
                ),
                false,
                500,
                0.2
        );
    }
}
