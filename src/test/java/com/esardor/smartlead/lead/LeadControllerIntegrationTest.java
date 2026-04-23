package com.esardor.smartlead.lead;

import com.esardor.smartlead.ai.LeadQualificationService;
import com.esardor.smartlead.message.InboundMessage;
import com.esardor.smartlead.message.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DisplayName("LeadController integration tests")
class LeadControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private MessageRepository messageRepository;

    @MockitoBean
    private LeadQualificationService leadQualificationService;

    private Lead savedLead;

    @BeforeEach
    void setUp() {
        leadRepository.deleteAll();
        messageRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(sharedHttpSession())
                .build();

        InboundMessage message = messageRepository.save(
                InboundMessage.builder()
                        .name("John Smith")
                        .email("john@techcorp.com")
                        .message("We need a demo urgently.")
                        .receivedAt(LocalDateTime.now())
                        .build()
        );

        savedLead = leadRepository.save(
                Lead.builder()
                        .message(message)
                        .title("Enterprise demo request")
                        .type(LeadType.DEMO_REQUEST)
                        .urgency(LeadUrgency.HIGH)
                        .summary("Team of 50 engineers needs demo urgently.")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    @Test
    @DisplayName("GET /api/v1/leads should return all qualified leads")
    void shouldReturnAllLeads() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Enterprise demo request"))
                .andExpect(jsonPath("$[0].type").value("DEMO_REQUEST"))
                .andExpect(jsonPath("$[0].urgency").value("HIGH"))
                .andExpect(jsonPath("$[0].senderName").value("John Smith"))
                .andExpect(jsonPath("$[0].senderEmail").value("john@techcorp.com"));
    }

    @Test
    @DisplayName("GET /api/v1/leads/{id} should return lead when found")
    void shouldReturnLeadById() throws Exception {
        mockMvc.perform(get("/api/v1/leads/{id}", savedLead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLead.getId()))
                .andExpect(jsonPath("$.title").value("Enterprise demo request"))
                .andExpect(jsonPath("$.type").value("DEMO_REQUEST"))
                .andExpect(jsonPath("$.urgency").value("HIGH"));
    }

    @Test
    @DisplayName("GET /api/v1/leads/{id} should return 404 when lead not found")
    void shouldReturn404WhenLeadNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/leads/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Lead with id 999 not found"));
    }

    @Test
    @DisplayName("GET /api/v1/leads/{id} should return 400 when id is invalid")
    void shouldReturn400WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/leads/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}