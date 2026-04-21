package com.esardor.smartlead.lead;

import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getMessage().getId(),
                lead.getMessage().getName(),
                lead.getMessage().getEmail(),
                lead.getTitle(),
                lead.getType(),
                lead.getUrgency(),
                lead.getSummary(),
                lead.getCreatedAt()
        );
    }

}
