package com.esardor.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

}
