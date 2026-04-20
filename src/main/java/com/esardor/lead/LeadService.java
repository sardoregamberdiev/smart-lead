package com.esardor.lead;

import com.esardor.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    public LeadService(
            LeadRepository leadRepository,
            LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
    }

    public List<LeadResponse> findAll() {
        return leadRepository.findAll()
                .stream()
                .map(leadMapper::toResponse)
                .toList();
    }

    public LeadResponse findById(Long id) {
        return leadRepository.findById(id)
                .map(leadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lead with id " + id + " not found"
                ));
    }
}
