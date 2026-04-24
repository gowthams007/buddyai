package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ChatService {

    @Value("${app.llm.api-key}")
    private String apiKey;

    @Value("${app.llm.base-url}")
    private String baseUrl;

    @Value("${app.llm.model}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String systemPrompt, String userMessage) {
        // Gemini URL format: https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}
        String url = String.format("%s/%s:generateContent?key=%s", baseUrl, modelName, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contents = requestBody.putArray("contents");

        // System prompt (Gemini treats this as a special role or prepended to first message)
        ObjectNode systemMsgNode = contents.addObject();
        systemMsgNode.put("role", "user");
        systemMsgNode.putArray("parts").addObject().put("text", "Instructions: " + systemPrompt);

        // User message
        ObjectNode userMsgNode = contents.addObject();
        userMsgNode.put("role", "user");
        userMsgNode.putArray("parts").addObject().put("text", userMessage);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        try {
            String response = restTemplate.postForObject(url, request, String.class);
            JsonNode root = objectMapper.readTree(response);
            // Gemini response path: candidates[0].content.parts[0].text
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "I'm sorry, I'm having trouble connecting to my brain right now.";
        }
    }
}
