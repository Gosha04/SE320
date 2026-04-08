package com.SE320.therapy.ai;

import java.util.Collection;
import java.util.List;

public interface VectorStore {

    void addAll(Collection<VectorDocument> documents);

    List<VectorMatch> similaritySearch(double[] queryEmbedding, int limit);

    long size();

    void clear();
}
