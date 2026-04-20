package com.esardor.message;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageMapper {

    public InboundMessage toEntity(MessageRequest request) {
        return InboundMessage.builder()
                .name(request.name())
                .email(request.email())
                .message(request.message())
                .receivedAt(LocalDateTime.now())
                .build();
    }

    public MessageResponse toResponse(InboundMessage entity) {
        return new MessageResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getMessage(),
                entity.getReceivedAt()
        );
    }

}
