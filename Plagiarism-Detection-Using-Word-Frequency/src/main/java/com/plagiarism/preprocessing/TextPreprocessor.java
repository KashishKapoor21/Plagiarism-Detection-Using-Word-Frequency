package com.plagiarism.preprocessing;

public class TextPreprocessor {

    public String normalizeText(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        text = text.toLowerCase();

        // Remove punctuation and special characters
        text = text.replaceAll("[^a-z0-9\\s]", "");

        // Remove extra spaces
        text = text.replaceAll("\\s+", " ").trim();

        return text;
    }
}