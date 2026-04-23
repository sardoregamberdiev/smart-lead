package com.esardor.smartlead.lead;

import com.esardor.smartlead.common.exception.ResourceNotFoundException;
import com.esardor.smartlead.message.InboundMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadService")
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private LeadMapper leadMapper;

    @InjectMocks
    private LeadService leadService;

    private InboundMessage buildMessage() {
        return InboundMessage.builder()
                .id(1L)
                .name("John Smith")
                .email("john@techcorp.com")
                .message("We need a demo")
                .receivedAt(LocalDateTime.now())
                .build();
    }

    private Lead buildLead(InboundMessage message) {
        return Lead.builder()
                .id(1L)
                .message(message)
                .title("Enterprise demo request")
                .type(LeadType.DEMO_REQUEST)
                .urgency(LeadUrgency.HIGH)
                .summary("Team needs demo urgently.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("should return all leads")
    void shouldReturnAllLeads() {
        InboundMessage message = buildMessage();
        Lead lead = buildLead(message);
        LeadResponse response = new LeadResponse(
                1L, 1L, "John Smith", "john@techcorp.com",
                "Enterprise demo request", LeadType.DEMO_REQUEST,
                LeadUrgency.HIGH, "Team needs demo urgently.",
                lead.getCreatedAt()
        );

        when(leadRepository.findAll()).thenReturn(List.of(lead));
        when(leadMapper.toResponse(lead)).thenReturn(response);

        List<LeadResponse> results = leadService.findAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("Enterprise demo request");
        assertThat(results.get(0).type()).isEqualTo(LeadType.DEMO_REQUEST);
        assertThat(results.get(0).urgency()).isEqualTo(LeadUrgency.HIGH);
    }

    @Test
    @DisplayName("should return lead by id when found")
    void shouldReturnLeadById() {
        InboundMessage message = buildMessage();
        Lead lead = buildLead(message);
        LeadResponse response = new LeadResponse(
                1L, 1L, "John Smith", "john@techcorp.com",
                "Enterprise demo request", LeadType.DEMO_REQUEST,
                LeadUrgency.HIGH, "Team needs demo urgently.",
                lead.getCreatedAt()
        );

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(leadMapper.toResponse(lead)).thenReturn(response);

        LeadResponse result = leadService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.senderEmail()).isEqualTo("john@techcorp.com");
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when lead not found")
    void shouldThrowWhenLeadNotFound() {
        when(leadRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}