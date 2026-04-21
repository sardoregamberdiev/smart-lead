package com.esardor.smartlead.lead;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response payload representing a qualified lead")
public record LeadResponse(

        @Schema(description = "Unique identifier of the lead", example = "1")
        Long id,

        @Schema(description = "ID of the originating inbound message", example = "1")
        Long messageId,

        @Schema(description = "Sender name from the original message", example = "John Smith")
        String senderName,

        @Schema(description = "Sender email from the original message", example = "john@techcorp.com")
        String senderEmail,

        @Schema(description = "AI-generated title for the lead",
                example = "Enterprise demo request from TechCorp")
        String title,

        @Schema(description = "Category of the lead")
        LeadType type,

        @Schema(description = "Urgency level determined by AI")
        LeadUrgency urgency,

        @Schema(description = "AI-generated summary of the opportunity")
        String summary,

        @Schema(description = "Timestamp when the lead was created")
        LocalDateTime createdAt
) {
}
