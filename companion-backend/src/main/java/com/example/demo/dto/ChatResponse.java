package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for chat messages
 * Contains AI-generated response and metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String response;
    private Long timestamp;
    
    public ChatResponse(String response) {
        this.response = response;
        this.timestamp = System.currentTimeMillis();
    }
}
