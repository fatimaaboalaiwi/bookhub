
package com.company.bookhub.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AiChatRequest(

        @NotBlank(message = "Message cannot be empty")
        @Size(max = 2000, message = "Message cannot exceed 2000 characters")
        String message,

        @NotBlank(message = "Conversation ID cannot be empty")
        @Size(min = 36, max = 36, message = "Conversation ID must be a valid UUID")
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "Conversation ID must be a valid UUID"
        )
        String conversationId

) {
}
