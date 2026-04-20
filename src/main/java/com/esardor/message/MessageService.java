package com.esardor.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Autowired
    MessageService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    public MessageResponse save(MessageRequest request) {
        InboundMessage message = messageMapper.toEntity(request);
        InboundMessage saved = messageRepository.save(message);
        return messageMapper.toResponse(saved);
    }

    public List<MessageResponse> findAll() {
        return messageRepository.findAll()
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }

}
