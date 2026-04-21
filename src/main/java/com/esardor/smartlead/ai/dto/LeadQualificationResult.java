package com.esardor.smartlead.ai.dto;

import com.esardor.smartlead.lead.LeadType;
import com.esardor.smartlead.lead.LeadUrgency;
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
