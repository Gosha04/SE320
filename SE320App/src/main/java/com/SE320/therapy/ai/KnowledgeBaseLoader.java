package com.SE320.therapy.ai;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class KnowledgeBaseLoader {

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final Path storePath;

    public KnowledgeBaseLoader(
            VectorStore vectorStore,
            EmbeddingService embeddingService,
            ObjectMapper objectMapper,
            ResourceLoader resourceLoader,
            @Value("${spring.ai.vectorstore.simple.store.path:./data/vector-store.json}") String storePath) {
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.storePath = Path.of(storePath);
    }

    @PostConstruct
    public void loadKnowledgeBase() {
        if (vectorStore instanceof SimpleVectorStore simpleVectorStore) {
            try {
                simpleVectorStore.load(storePath);
                if (vectorStore.size() > 0) {
                    return;
                }
            } catch (IOException ignored) {
                vectorStore.clear();
            }
        }

        vectorStore.clear();
        vectorStore.addAll(loadDocuments("classpath:knowledge-base/distortions.json", "distortion"));
        vectorStore.addAll(loadDocuments("classpath:knowledge-base/cbt-techniques.json", "technique"));
        vectorStore.addAll(loadDocuments("classpath:knowledge-base/crisis-protocols.json", "crisis"));

        if (vectorStore instanceof SimpleVectorStore simpleVectorStore) {
            try {
                simpleVectorStore.save(storePath);
            } catch (IOException ignored) {
                // The in-memory store is still usable even if persistence fails.
            }
        }
    }

    private VectorDocument knowledgeDocument(String id, String content, Map<String, String> metadata) {
        return new VectorDocument(id, content, metadata, embeddingService.embed(content));
    }

    private List<VectorDocument> loadDocuments(String resourceLocation, String defaultCategory) {
        Resource resource = resourceLoader.getResource(resourceLocation);
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode documentsNode = root.path("documents");
            if (!documentsNode.isArray()) {
                return List.of();
            }

            List<VectorDocument> documents = new java.util.ArrayList<>();
            for (JsonNode node : documentsNode) {
                String id = node.path("id").asText();
                String content = node.path("content").asText();
                String category = node.path("category").asText(defaultCategory);
                String topic = node.path("topic").asText(id);
                if (!id.isBlank() && !content.isBlank()) {
                    documents.add(knowledgeDocument(
                            id,
                            content,
                            Map.of("category", category, "topic", topic)));
                }
            }
            return documents;
        } catch (IOException ex) {
            return List.of();
        }
    }
}
