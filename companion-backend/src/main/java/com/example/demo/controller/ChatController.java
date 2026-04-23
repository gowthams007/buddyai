package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.AgentOrchestrator;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Chat Controller for BuddyAI
 * Handles all chat-related endpoints
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    private final AgentOrchestrator agentOrchestrator;
    private final UserService userService;
    private final com.example.demo.repository.ConversationRepository conversationRepository;

    public ChatController(AgentOrchestrator agentOrchestrator, UserService userService, com.example.demo.repository.ConversationRepository conversationRepository) {
        this.agentOrchestrator = agentOrchestrator;
        this.userService = userService;
        this.conversationRepository = conversationRepository;
    }

    /**
     * Process a chat message and return AI response
     * @param request ChatRequest containing userId and message
     * @return ChatResponse with AI-generated response
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        try {
            logger.info("Processing chat message for user: {}", request.getUserId());
            
            User user = userService.getOrCreateUser(request.getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("User not found with ID: " + request.getUserId());
            }
            
            String response = agentOrchestrator.processUserMessage(user, request.getMessage());
            logger.info("Chat response generated successfully for user: {}", request.getUserId());
            
            return ResponseEntity.ok(new ChatResponse(response));
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            
            logger.error("Error processing chat message", e);
            throw new RuntimeException("Failed to process chat message: " + e.getMessage());
        }
    }

    /**
     * Get chat history for a user
     * @param userId User ID
     * @return List of conversations
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long userId) {
        try {
            logger.info("Fetching chat history for user: {}", userId);
            User user = userService.getUserById(userId);
            if (user == null) {
                throw new ResourceNotFoundException("User not found with ID: " + userId);
            }
            return ResponseEntity.ok(conversationRepository.findAllByUserIdOrderByCreatedAtDesc(userId));
        } catch (Exception e) {
            logger.error("Error fetching chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching chat history: " + e.getMessage());
        }
    }
}
