package com.esardor.lead;

import com.esardor.message.InboundMessage;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead")
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

    public Lead() {
    }

    public Lead(InboundMessage message, String title, LeadType type, LeadUrgency urgency, String summary, LocalDateTime createdAt) {
        this.message = message;
        this.title = title;
        this.type = type;
        this.urgency = urgency;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InboundMessage getMessage() {
        return message;
    }

    public void setMessage(InboundMessage message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LeadType getType() {
        return type;
    }

    public void setType(LeadType type) {
        this.type = type;
    }

    public LeadUrgency getUrgency() {
        return urgency;
    }

    public void setUrgency(LeadUrgency urgency) {
        this.urgency = urgency;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Lead{" +
                "id=" + id +
                ", message=" + message +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", urgency=" + urgency +
                ", summary='" + summary + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}