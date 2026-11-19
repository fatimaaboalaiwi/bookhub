
package com.company.bookhub.ai.controller;

import com.company.bookhub.ai.dto.AiChatRequest;
import com.company.bookhub.ai.dto.AiChatResponse;
import com.company.bookhub.ai.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(
        name = "AI Assistant",
        description = "APIs for interacting with the BookHub AI assistant"
)
@SecurityRequirement(name = "bearerAuth")
public class AiChatController {

    private final AiChatService aiChatService;

    @Operation(
            summary = "Chat with BookHub AI",
            description = "Sends a message to the local AI model and returns its response."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AI response generated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or empty message or conversation ID"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Missing BOOK_READ permission"
            )
    })
    @PreAuthorize("hasAuthority('BOOK_READ')")
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @Valid @RequestBody AiChatRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        return ResponseEntity.ok(
                aiChatService.chat(
                        request.message(),
                        request.conversationId(),
                        userDetails.getUsername()
                )
        );
    }
}
