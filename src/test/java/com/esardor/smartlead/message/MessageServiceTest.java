package com.esardor.smartlead.message;

import com.esardor.smartlead.ai.LeadQualificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private LeadQualificationService leadQualificationService;

    @InjectMocks
    private MessageService messageService;

    @Test
    @DisplayName("should save message and trigger async qualification")
    void shouldSaveMessageAndTriggerQualification() {
        MessageRequest request = new MessageRequest(
                "John Smith",
                "john@techcorp.com",
                "We need a demo of your enterprise plan next week."
        );

        InboundMessage entity = InboundMessage.builder()
                .id(1L)
                .name("John Smith")
                .email("john@techcorp.com")
                .message("We need a demo of your enterprise plan next week.")
                .receivedAt(LocalDateTime.now())
                .build();

        MessageResponse expectedResponse = new MessageResponse(
                1L, "John Smith", "john@techcorp.com",
                "We need a demo of your enterprise plan next week.",
                entity.getReceivedAt()
        );

        when(messageMapper.toEntity(request)).thenReturn(entity);
        when(messageRepository.save(entity)).thenReturn(entity);
        when(messageMapper.toResponse(entity)).thenReturn(expectedResponse);

        MessageResponse response = messageService.save(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("John Smith");
        assertThat(response.email()).isEqualTo("john@techcorp.com");

        verify(messageRepository, times(1)).save(entity);
        verify(leadQualificationService, times(1)).qualify(entity);
    }

    @Test
    @DisplayName("should return all messages")
    void shouldReturnAllMessages() {
        InboundMessage message1 = InboundMessage.builder()
                .id(1L).name("John").email("john@test.com")
                .message("Hello").receivedAt(LocalDateTime.now())
                .build();

        InboundMessage message2 = InboundMessage.builder()
                .id(2L).name("Jane").email("jane@test.com")
                .message("Hi there").receivedAt(LocalDateTime.now())
                .build();

        when(messageRepository.findAll()).thenReturn(List.of(message1, message2));
        when(messageMapper.toResponse(message1)).thenReturn(
                new MessageResponse(1L, "John", "john@test.com",
                        "Hello", message1.getReceivedAt()));
        when(messageMapper.toResponse(message2)).thenReturn(
                new MessageResponse(2L, "Jane", "jane@test.com",
                        "Hi there", message2.getReceivedAt()));

        List<MessageResponse> responses = messageService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("John");
        assertThat(responses.get(1).name()).isEqualTo("Jane");
    }
}