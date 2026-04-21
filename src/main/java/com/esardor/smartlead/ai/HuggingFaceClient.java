package com.esardor.smartlead.ai;

import com.esardor.smartlead.ai.dto.ChatRequest;
import com.esardor.smartlead.ai.dto.ChatResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("v1/chat/completions")
public interface HuggingFaceClient {

    @PostExchange
    ChatResponse complete(@RequestBody ChatRequest request);

}
