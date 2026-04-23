package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for chat messages
 * Validates user input before processing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 1, max = 5000, message = "Message must be between 1 and 5000 characters")
    private String message;
}
