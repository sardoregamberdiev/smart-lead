package com.esardor.smartlead.ai;

import com.esardor.smartlead.ai.dto.ChatRequest;
import com.esardor.smartlead.ai.dto.ChatResponse;
import com.esardor.smartlead.ai.dto.Choice;
import com.esardor.smartlead.ai.dto.Message;
import com.esardor.smartlead.lead.Lead;
import com.esardor.smartlead.lead.LeadRepository;
import com.esardor.smartlead.lead.LeadType;
import com.esardor.smartlead.lead.LeadUrgency;
import com.esardor.smartlead.message.InboundMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadQualificationService")
class LeadQualificationServiceTest {

    @Mock
    private HuggingFaceClient huggingFaceClient;

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadQualificationService leadQualificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                leadQualificationService,
                "model",
                "test-model"
        );

        ReflectionTestUtils.setField(
                leadQualificationService,
                "objectMapper",
                new ObjectMapper()
        );
    }

    private InboundMessage buildMessage(String text) {
        return InboundMessage.builder()
                .id(1L)
                .name("John Smith")
                .email("john@techcorp.com")
                .message(text)
                .receivedAt(LocalDateTime.now())
                .build();
    }

    private ChatResponse buildAiResponse(String json) {
        return new ChatResponse(
                List.of(new Choice(
                        new Message("assistant", json)
                ))
        );
    }

    @Nested
    @DisplayName("when message is a qualified lead")
    class WhenQualifiedLead {

        @Test
        @DisplayName("should save lead with correct fields")
        void shouldSaveLeadWithCorrectFields() {
            String aiJson = """
                    {
                      "isLead": true,
                      "title": "Enterprise demo request",
                      "type": "DEMO_REQUEST",
                      "urgency": "HIGH",
                      "summary": "Team of 50 engineers with approved budget."
                    }
                    """;

            InboundMessage message = buildMessage(
                    "We are 50 engineers, budget approved, need a demo."
            );
            when(huggingFaceClient.complete(any(ChatRequest.class)))
                    .thenReturn(buildAiResponse(aiJson));

            leadQualificationService.qualify(message);

            ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
            verify(leadRepository, times(1)).save(leadCaptor.capture());

            Lead savedLead = leadCaptor.getValue();
            assertThat(savedLead.getTitle()).isEqualTo("Enterprise demo request");
            assertThat(savedLead.getType()).isEqualTo(LeadType.DEMO_REQUEST);
            assertThat(savedLead.getUrgency()).isEqualTo(LeadUrgency.HIGH);
            assertThat(savedLead.getSummary())
                    .isEqualTo("Team of 50 engineers with approved budget.");
            assertThat(savedLead.getMessage()).isEqualTo(message);
            assertThat(savedLead.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should save lead when AI wraps response in markdown code block")
        void shouldHandleMarkdownWrappedJson() {
            String aiJson = """
                    ```json
                                        {
                                          "isLead": true,
                                          "title": "Pricing inquiry",
                                          "type": "PRICING_INQUIRY",
                                          "urgency": "HIGH",
                                          "summary": "Needs pricing by Friday."
                                        }
                    ```
                    """;

            InboundMessage message = buildMessage(
                    "Send me pricing, need to decide by Friday."
            );
            when(huggingFaceClient.complete(any(ChatRequest.class)))
                    .thenReturn(buildAiResponse(aiJson));

            leadQualificationService.qualify(message);

            verify(leadRepository, times(1)).save(any(Lead.class));
        }
    }

    @Nested
    @DisplayName("when message is not a lead")
    class WhenNotLead {

        @Test
        @DisplayName("should not save lead")
        void shouldNotSaveLead() {
            String aiJson = """
                    {
                      "isLead": false,
                      "title": null,
                      "type": null,
                      "urgency": null,
                      "summary": null
                    }
                    """;

            InboundMessage message = buildMessage("Your website looks great!");
            when(huggingFaceClient.complete(any(ChatRequest.class)))
                    .thenReturn(buildAiResponse(aiJson));

            leadQualificationService.qualify(message);

            verify(leadRepository, never()).save(any(Lead.class));
        }
    }

    @Nested
    @DisplayName("when AI call fails")
    class WhenAiFails {

        @Test
        @DisplayName("should not throw exception and not save lead")
        void shouldHandleExceptionGracefully() {
            InboundMessage message = buildMessage("Some message");
            when(huggingFaceClient.complete(any(ChatRequest.class)))
                    .thenThrow(new RuntimeException("HuggingFace API unavailable"));

            leadQualificationService.qualify(message);

            verify(leadRepository, never()).save(any(Lead.class));
        }

        @Test
        @DisplayName("should not throw exception when AI returns malformed JSON")
        void shouldHandleMalformedJson() {
            InboundMessage message = buildMessage("Some message");
            when(huggingFaceClient.complete(any(ChatRequest.class)))
                    .thenReturn(buildAiResponse("this is not json at all"));

            leadQualificationService.qualify(message);

            verify(leadRepository, never()).save(any(Lead.class));
        }
    }
}