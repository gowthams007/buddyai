package com.example.demo.service;

import com.example.demo.entity.Memory;
import com.example.demo.entity.User;
import com.example.demo.repository.MemoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoryService {

    private final MemoryRepository memoryRepository;

    @Value("${app.llm.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MemoryService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public void storeMemory(User user, String content) {
        List<Double> embedding = generateEmbedding(content);
        try {
            String embeddingJson = objectMapper.writeValueAsString(embedding);
            Memory memory = Memory.builder()
                    .user(user)
                    .content(content)
                    .embeddingJson(embeddingJson)
                    .build();
            memoryRepository.save(memory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> retrieveRelevantMemories(User user, String query, int limit) {
        List<Double> queryEmbedding = generateEmbedding(query);
        List<Memory> allMemories = memoryRepository.findByUserId(user.getId());

        return allMemories.stream()
                .map(memory -> {
                    try {
                        List<Double> memEmbedding = objectMapper.readValue(memory.getEmbeddingJson(), new TypeReference<>() {});
                        double similarity = cosineSimilarity(queryEmbedding, memEmbedding);
                        return new MemoryMatch(memory.getContent(), similarity);
                    } catch (Exception e) {
                        return new MemoryMatch(memory.getContent(), -1.0);
                    }
                })
                .sorted(Comparator.comparingDouble(MemoryMatch::getSimilarity).reversed())
                .limit(limit)
                .map(MemoryMatch::getContent)
                .collect(Collectors.toList());
    }

    private List<Double> generateEmbedding(String text) {
        String url = "https://api.openai.com/v1/embeddings";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "text-embedding-ada-002");
        requestBody.put("input", text);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
        try {
            String response = restTemplate.postForObject(url, request, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");
            List<Double> embedding = new ArrayList<>();
            for (JsonNode node : embeddingNode) {
                embedding.add(node.asDouble());
            }
            return embedding;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // fallback
        }
    }

    private double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA.isEmpty() || vectorB.isEmpty() || vectorA.size() != vectorB.size()) return 0.0;
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static class MemoryMatch {
        private String content;
        private double similarity;

        public MemoryMatch(String content, double similarity) {
            this.content = content;
            this.similarity = similarity;
        }

        public String getContent() { return content; }
        public double getSimilarity() { return similarity; }
    }
}
