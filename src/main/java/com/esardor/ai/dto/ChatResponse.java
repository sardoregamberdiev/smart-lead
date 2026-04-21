package com.esardor.ai.dto;

import java.util.List;

public record ChatResponse(
        List<Choice> choices
) {
    public record Choice(Message message) {
    }

    public String content() {
        if (choices == null || choices.isEmpty()) return "";
        return choices.getFirst().message.content();
    }
}
