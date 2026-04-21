package com.esardor.smartlead.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<InboundMessage, Long> {
}
