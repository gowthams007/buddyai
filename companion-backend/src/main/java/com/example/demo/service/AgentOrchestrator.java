package com.example.demo.service;

import com.example.demo.entity.Conversation;
import com.example.demo.entity.User;
import com.example.demo.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentOrchestrator {

    private final ChatService chatService;
    private final MemoryService memoryService;
    private final ConversationRepository conversationRepository;

    public AgentOrchestrator(ChatService chatService, MemoryService memoryService, ConversationRepository conversationRepository) {
        this.chatService = chatService;
        this.memoryService = memoryService;
        this.conversationRepository = conversationRepository;
    }

    public String processUserMessage(User user, String userMessage) {
        // 1. Retrieve relevant memories
        List<String> memories = memoryService.retrieveRelevantMemories(user, userMessage, 3);
        String memoryContext = memories.isEmpty() ? "No specific memory found." : String.join("\n", memories);

        // 2. Fetch recent conversation history
        List<Conversation> recentHistory = conversationRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());
        String historyContext = recentHistory.stream()
                .map(c -> "User: " + c.getUserMessage() + "\nAI: " + c.getAiResponse())
                .collect(Collectors.joining("\n"));

        // 3. Construct the prompt
        String systemPrompt = "You are a friendly personal AI companion. Your goal is to assist the user with tasks, goals, and daily planning.\n" +
                "Use the following past memories if relevant:\n" + memoryContext + "\n\n" +
                "Recent conversation history:\n" + historyContext;

        // 4. Get response from LLM
        String aiResponse = chatService.generateResponse(systemPrompt, userMessage);

        // 5. Store conversation
        Conversation conversation = Conversation.builder()
                .user(user)
                .userMessage(userMessage)
                .aiResponse(aiResponse)
                .build();
        conversationRepository.save(conversation);

        // 6. Store significant facts to memory (simplification: we'll just store the user message if it's long enough, 
        // in a real system we'd use another LLM call to extract facts)
        if (userMessage.length() > 20) {
            memoryService.storeMemory(user, userMessage);
        }

        return aiResponse;
    }
}
