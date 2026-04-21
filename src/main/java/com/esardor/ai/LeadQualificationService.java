package com.esardor.ai;

import com.esardor.ai.dto.ChatRequest;
import com.esardor.ai.dto.ChatResponse;
import com.esardor.ai.dto.LeadQualificationResult;
import com.esardor.lead.Lead;
import com.esardor.lead.LeadRepository;
import com.esardor.message.InboundMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LeadQualificationService {

    private static final Logger log = LoggerFactory.getLogger(LeadQualificationService.class);

    private static final String SYSTEM_PROMPT = """
            You are a lead qualification assistant for a B2B SaaS company.
            Your job is to analyze inbound contact form messages and determine
            if they represent a genuine sales opportunity.
            
            A message IS a lead if it shows:
            - Intent to purchase, subscribe, or upgrade
            - Request for a demo or product walkthrough
            - Inquiry about pricing or plans
            - Interest in partnership or integration
            - Billing issue from an existing customer needing support
            
            A message is NOT a lead if it is:
            - A general compliment or feedback with no buying intent
            - A basic support question (password reset, how-to questions)
            - Spam or irrelevant content
            - A simple informational question (office hours, shipping)
            
            You MUST respond with ONLY a valid JSON object. No explanation.
            No markdown. No code blocks. Just raw JSON.
            
            If it IS a lead, respond with:
            {
              "isLead": true,
              "title": "short descriptive title for the sales team",
              "type": "one of: DEMO_REQUEST, PRICING_INQUIRY, PARTNERSHIP, SUPPORT, OTHER",
              "urgency": "one of: LOW, MEDIUM, HIGH",
              "summary": "2-3 sentence summary of the opportunity for the sales team"
            }
            
            If it is NOT a lead, respond with:
            {
              "isLead": false,
              "title": null,
              "type": null,
              "urgency": null,
              "summary": null
            }
            
            Urgency rules:
            - HIGH: mentions deadlines, budget approved, ready to buy, ASAP
            - MEDIUM: evaluating options, no specific deadline
            - LOW: early stage curiosity, vague interest
            """;
    private final HuggingFaceClient huggingFaceClient;
    private final LeadRepository leadRepository;
    private final ObjectMapper objectMapper;

    @Value("${ai.huggingface.model}")
    private String model;


    public LeadQualificationService(HuggingFaceClient huggingFaceClient, LeadRepository leadRepository, ObjectMapper objectMapper) {
        this.huggingFaceClient = huggingFaceClient;
        this.leadRepository = leadRepository;
        this.objectMapper = objectMapper;
    }

    @Async("taskExecutor")
    public void qualify(InboundMessage message) {
        log.info("Starting lead qualification for message id : {}", message.getId());

        try {
            ChatRequest request = ChatRequest.of(
                    model,
                    SYSTEM_PROMPT,
                    message.getMessage()
            );

            ChatResponse response = huggingFaceClient.complete(request);
            String content = response.content();

            log.debug("AI raw response for message id : {} : {}", message.getId(), content);

            LeadQualificationResult result = parseResult(content);

            if (result.isLead()) {
                saveLead(message, result);
                log.info("Message id {} qualified as lead - type: {}, urgency: {}", message.getId(), result.type(), result.urgency());
            } else {
                log.info("Message id {} not qualified as lead", message.getId());
            }
        } catch (Exception ex) {
            log.error("Lead qualification failed for message id : {}. Reason: {}", message.getId(), ex.getMessage(), ex);
        }
    }

    private LeadQualificationResult parseResult(String content) throws Exception {
        String cleaned = content
                .trim()
                .replaceAll("(?s)```json", "")
                .replaceAll("(?s)```", "")
                .trim();
        return objectMapper.readValue(cleaned, LeadQualificationResult.class);
    }

    private void saveLead(InboundMessage message, LeadQualificationResult result) {
        Lead lead = Lead.builder()
                .message(message)
                .title(result.title())
                .type(result.type())
                .urgency(result.urgency())
                .summary(result.summary())
                .createdAt(LocalDateTime.now())
                .build();
        leadRepository.save(lead);
    }

}
