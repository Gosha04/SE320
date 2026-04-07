package com.SE320.therapy.ai;

import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class HashingEmbeddingService implements EmbeddingService {

    private static final int DIMENSIONS = 128;

    @Override
    public double[] embed(String text) {
        double[] vector = new double[DIMENSIONS];
        if (text == null || text.isBlank()) {
            return vector;
        }

        String normalized = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .trim();

        if (normalized.isEmpty()) {
            return vector;
        }

        for (String token : normalized.split("\\s+")) {
            int index = Math.floorMod(token.hashCode(), DIMENSIONS);
            vector[index] += 1.0d;
        }

        normalize(vector);
        return vector;
    }

    private void normalize(double[] vector) {
        double magnitude = 0.0d;
        for (double value : vector) {
            magnitude += value * value;
        }

        if (magnitude == 0.0d) {
            return;
        }

        double scale = Math.sqrt(magnitude);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / scale;
        }
    }
}
