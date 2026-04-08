package com.SE320.therapy.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SimpleVectorStore implements VectorStore {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<VectorDocument> documents = new CopyOnWriteArrayList<>();

    @Override
    public void addAll(Collection<VectorDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        this.documents.addAll(documents);
    }

    @Override
    public List<VectorMatch> similaritySearch(double[] queryEmbedding, int limit) {
        if (queryEmbedding == null || queryEmbedding.length == 0 || limit <= 0) {
            return List.of();
        }

        List<VectorMatch> matches = new ArrayList<>();
        for (VectorDocument document : documents) {
            double score = cosineSimilarity(queryEmbedding, document.getEmbedding());
            matches.add(new VectorMatch(document, score));
        }

        matches.sort(Comparator.comparingDouble(VectorMatch::score).reversed());
        return matches.stream().limit(limit).toList();
    }

    @Override
    public long size() {
        return documents.size();
    }

    @Override
    public void clear() {
        documents.clear();
    }

    public void save(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), documents);
    }

    public void load(Path path) throws IOException {
        if (!Files.exists(path) || Files.size(path) == 0L) {
            return;
        }
        List<VectorDocument> loadedDocuments = objectMapper.readValue(
                path.toFile(),
                new TypeReference<List<VectorDocument>>() {
                });
        documents.clear();
        documents.addAll(loadedDocuments);
    }

    private double cosineSimilarity(double[] left, double[] right) {
        int length = Math.min(left.length, right.length);
        if (length == 0) {
            return 0.0d;
        }

        double dot = 0.0d;
        double leftMagnitude = 0.0d;
        double rightMagnitude = 0.0d;

        for (int i = 0; i < length; i++) {
            dot += left[i] * right[i];
            leftMagnitude += left[i] * left[i];
            rightMagnitude += right[i] * right[i];
        }

        if (leftMagnitude == 0.0d || rightMagnitude == 0.0d) {
            return 0.0d;
        }

        return dot / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }
}
