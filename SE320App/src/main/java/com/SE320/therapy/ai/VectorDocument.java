package com.SE320.therapy.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class VectorDocument {

    private final String id;
    private final String content;
    private final Map<String, String> metadata;
    private final double[] embedding;

    public VectorDocument(String id, String content, Map<String, String> metadata, double[] embedding) {
        this.id = id;
        this.content = content;
        this.metadata = metadata == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
        this.embedding = embedding == null ? new double[0] : embedding.clone();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public double[] getEmbedding() {
        return embedding.clone();
    }
}
