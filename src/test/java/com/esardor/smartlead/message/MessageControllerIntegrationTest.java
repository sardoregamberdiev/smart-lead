package com.esardor.smartlead.message;

import com.esardor.smartlead.ai.LeadQualificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DisplayName("MessageController integration tests")
class MessageControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @MockitoBean
    private LeadQualificationService leadQualificationService;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(sharedHttpSession())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/messages should return 201 with saved message")
    void shouldSubmitMessageAndReturn201() throws Exception {
        doNothing().when(leadQualificationService).qualify(any());

        MessageRequest request = new MessageRequest(
                "John Smith",
                "john@techcorp.com",
                "We are a team of 50 engineers and need a demo."
        );

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@techcorp.com"))
                .andExpect(jsonPath("$.receivedAt").exists());
    }

    @Test
    @DisplayName("POST /api/v1/messages should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        MessageRequest request = new MessageRequest(
                "",
                "john@techcorp.com",
                "We need a demo of your platform."
        );

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details", hasItem("Name is required")));
    }

    @Test
    @DisplayName("POST /api/v1/messages should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        MessageRequest request = new MessageRequest(
                "John Smith",
                "not-an-email",
                "We need a demo of your platform."
        );

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details",
                        hasItem("Email must be a valid email address")));
    }

    @Test
    @DisplayName("POST /api/v1/messages should return 400 when message is too short")
    void shouldReturn400WhenMessageIsTooShort() throws Exception {
        MessageRequest request = new MessageRequest(
                "John Smith",
                "john@techcorp.com",
                "Hi"
        );

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details",
                        hasItem("Message must be at least 10 characters")));
    }

    @Test
    @DisplayName("GET /api/v1/messages should return exactly 1 message")
    void shouldReturnAllMessages() throws Exception {
        doNothing().when(leadQualificationService).qualify(any());

        MessageRequest request = new MessageRequest(
                "Jane Doe",
                "jane@company.com",
                "Interested in your enterprise plan pricing."
        );

        mockMvc.perform(post("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/v1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Jane Doe"))
                .andExpect(jsonPath("$[0].email").value("jane@company.com"));
    }
}