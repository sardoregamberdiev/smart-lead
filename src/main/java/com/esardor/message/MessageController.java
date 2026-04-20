package com.esardor.message;

import com.esardor.common.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messages", description = "API/Endpoints for submitting and viewing inbound messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Submit a new inbound message",
            description = "Accepts a contact form message and triggers async AI lead qualification"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MessageResponse> submit(@RequestBody MessageRequest messageRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.save(messageRequest));
    }

    @Operation(
            summary = "Get all inbound messages",
            description = "Returns all messages submitted through the contact form"
    )
    @ApiResponse(responseCode = "200", description = "List of messages returned successfully")
    @GetMapping
    public ResponseEntity<List<MessageResponse>> findAll() {
        return ResponseEntity.ok(messageService.findAll());
    }
}
