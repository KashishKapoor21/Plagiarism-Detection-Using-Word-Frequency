package com.plagiarism.analysis;

import java.util.HashSet;
import java.util.Set;

public class PhraseMatcher {

    public Set<String> findCommonPhrases(String text1, String text2) {

        Set<String> phrases1 = generateNGrams(text1);
        Set<String> phrases2 = generateNGrams(text2);

        phrases1.retainAll(phrases2);

        return phrases1;
    }

    private Set<String> generateNGrams(String text) {

        Set<String> ngrams = new HashSet<>();

        if (text == null || text.isBlank()) {
            return ngrams;
        }

        // Split text into sentences
        String[] sentences = text.split("[.!?\\n]+");

        int n = 3; // Three-word phrases

        for (String sentence : sentences) {

            sentence = sentence.toLowerCase();

            sentence = sentence.replaceAll("[^a-z0-9\\s]", "");

            sentence = sentence.replaceAll("\\s+", " ").trim();

            if (sentence.isBlank()) {
                continue;
            }

            String[] words = sentence.split("\\s+");

            for (int i = 0; i <= words.length - n; i++) {

                String phrase =
                        words[i] + " " +
                                words[i + 1] + " " +
                                words[i + 2];

                ngrams.add(phrase);
            }
        }

        return ngrams;
    }
}