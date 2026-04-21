package com.esardor.message;

import com.esardor.ai.LeadQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final LeadQualificationService leadQualificationService;

    @Autowired
    MessageService(MessageRepository messageRepository, MessageMapper messageMapper, LeadQualificationService leadQualificationService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.leadQualificationService = leadQualificationService;
    }

    public MessageResponse save(MessageRequest request) {
        InboundMessage message = messageMapper.toEntity(request);
        InboundMessage saved = messageRepository.save(message);

        // triggers in background - client gets 201 immediately"
        leadQualificationService.qualify(saved);

        return messageMapper.toResponse(saved);
    }

    public List<MessageResponse> findAll() {
        return messageRepository.findAll()
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }


}
