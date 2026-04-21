package com.esardor.ai.dto;

import com.esardor.lead.LeadType;
import com.esardor.lead.LeadUrgency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LeadQualificationResult(
        boolean isLead,
        String title,
        LeadType type,
        LeadUrgency urgency,
        String summary
) {
}
