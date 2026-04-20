package com.esardor.lead;

import com.esardor.message.InboundMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lead",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_lead_message_id",
                columnNames = {"message_id"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "message_id", nullable = false)
    private InboundMessage message;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadUrgency urgency;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}