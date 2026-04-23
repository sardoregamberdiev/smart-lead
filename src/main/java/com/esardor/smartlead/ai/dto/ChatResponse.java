package com.esardor.smartlead.ai.dto;

import java.util.List;

public record ChatResponse(
        List<Choice> choices
) {

    public String content() {
        if (choices == null || choices.isEmpty()) return "";
        return choices.getFirst().message().content();
    }
}
