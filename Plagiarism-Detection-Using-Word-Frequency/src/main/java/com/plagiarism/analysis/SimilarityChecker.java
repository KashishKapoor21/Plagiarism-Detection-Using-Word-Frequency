package com.plagiarism.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimilarityChecker {

    public double calculateSimilarity(
            Map<String, Integer> map1,
            Map<String, Integer> map2) {

        Set<String> allWords = new HashSet<>();

        allWords.addAll(map1.keySet());
        allWords.addAll(map2.keySet());

        double dotProduct = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;

        for (String word : allWords) {

            int a = map1.getOrDefault(word, 0);
            int b = map2.getOrDefault(word, 0);

            dotProduct += (double) a * b;

            magnitudeA += (double) a * a;
            magnitudeB += (double) b * b;
        }

        if (magnitudeA == 0 || magnitudeB == 0) {
            return 0.0;
        }

        double similarity =
                dotProduct /
                        (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));

        return similarity * 100;
    }

    public String getSimilarityLevel(double similarity) {

        if (similarity < 0 || similarity > 100) {
            throw new IllegalArgumentException(
                    "Similarity must be between 0 and 100");
        }

        if (similarity <= 20) {
            return "Low Similarity";
        } else if (similarity <= 40) {
            return "Moderate Similarity";
        } else if (similarity <= 60) {
            return "Significant Similarity";
        } else if (similarity <= 80) {
            return "High Similarity";
        } else {
            return "Very High Similarity";
        }
    }
}