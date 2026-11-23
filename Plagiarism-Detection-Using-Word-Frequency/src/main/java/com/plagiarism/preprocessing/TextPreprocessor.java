package com.plagiarism.preprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TextPreprocessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "is", "am", "are", "was", "were",
            "a", "an", "and", "or", "but",
            "in", "on", "at", "to", "for", "of",
            "this", "that", "these", "those",
            "it", "he", "she", "they", "we", "you",
            "with", "from", "by", "as"
    ));

    // Normalize text
    public String normalizeText(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        // Convert text to lowercase
        text = text.toLowerCase();

        // Remove punctuation and special characters
        text = text.replaceAll("[^a-z0-9\\s]", "");

        // Remove extra spaces
        text = text.replaceAll("\\s+", " ").trim();

        return text;
    }

    // Remove common English stop words
    public String removeStopWords(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        String[] words = text.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {

            if (!STOP_WORDS.contains(word)) {
                result.append(word).append(" ");
            }
        }

        return result.toString().trim();
    }

    // Complete preprocessing pipeline
    public String preprocess(String text) {

        String normalizedText = normalizeText(text);

        return removeStopWords(normalizedText);
    }
}