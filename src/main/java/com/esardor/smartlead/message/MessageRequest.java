package com.esardor.smartlead.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for submitting an inbound message")
public record MessageRequest(

        @Schema(description = "Full name of the sender", example = "John Smith")
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Schema(description = "Email address of the sender", example = "john@techcorp.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @Schema(
                description = "The message content to be analyzed for lead qualification",
                example = "We are a team of 50 engineers and would like to schedule a demo"
        )
        @NotBlank(message = "Message is required")
        @Size(min = 10, message = "Message must be at least 10 characters")
        String message) {
}
