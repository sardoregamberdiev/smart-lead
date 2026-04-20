package com.esardor.lead;

import com.esardor.common.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Leads", description = "Endpoints for viewing AI-qualified leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @Operation(
            summary = "Get all qualified leads",
            description = "Returns all leads that were qualified by the AI model"
    )
    @ApiResponse(responseCode = "200", description = "List of leads returned successfully")
    @GetMapping
    public ResponseEntity<List<LeadResponse>> findAll() {
        return ResponseEntity.ok(leadService.findAll());
    }

    @Operation(
            summary = "Get a lead by ID",
            description = "Returns a single qualified lead by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead found and returned"),
            @ApiResponse(responseCode = "404", description = "Lead not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.findById(id));
    }
}
