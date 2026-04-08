package com.SE320.therapy.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class SimpleVectorStoreTest {

    @Test
    void saveAndLoad_roundTripsDocuments() throws Exception {
        SimpleVectorStore originalStore = new SimpleVectorStore();
        originalStore.addAll(List.of(
                new VectorDocument(
                        "doc-1",
                        "Behavioral activation builds momentum.",
                        java.util.Map.of("category", "technique"),
                        new double[] { 0.1d, 0.2d, 0.3d })));

        Path tempFile = Files.createTempFile("vector-store", ".json");
        try {
            originalStore.save(tempFile);

            SimpleVectorStore loadedStore = new SimpleVectorStore();
            loadedStore.load(tempFile);

            assertEquals(1L, loadedStore.size());
            assertEquals(
                    1,
                    loadedStore.similaritySearch(new double[] { 0.1d, 0.2d, 0.3d }, 1).size());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
