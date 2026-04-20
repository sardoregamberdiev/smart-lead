package com.esardor.message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String name,
        String email,
        String message,
        LocalDateTime receivedAt
) {
}
