package com.esardor.message;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response payload representing a stored inbound message")
public record MessageResponse(

        @Schema(description = "Unique identifier of the message", example = "1")
        Long id,

        @Schema(description = "Full name of the sender", example = "John Smith")
        String name,

        @Schema(description = "Email address of the sender", example = "john@techcorp.com")
        String email,

        @Schema(description = "The original message content")
        String message,

        @Schema(description = "Timestamp when the message was received")
        LocalDateTime receivedAt
) {
}