package com.esardor.ai;

import com.esardor.ai.dto.ChatRequest;
import com.esardor.ai.dto.ChatResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("v1/chat/completions")
public interface HuggingFaceClient {

    @PostExchange
    ChatResponse complete(@RequestBody ChatRequest request);

}
