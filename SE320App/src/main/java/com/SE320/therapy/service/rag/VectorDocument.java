package com.SE320.therapy.service.rag;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VectorDocument {

    private final String id;
    private final String content;
    private final Map<String, String> metadata;
    private final double[] embedding;

    @JsonCreator
    public VectorDocument(
            @JsonProperty("id") String id,
            @JsonProperty("content") String content,
            @JsonProperty("metadata") Map<String, String> metadata,
            @JsonProperty("embedding") double[] embedding) {
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
