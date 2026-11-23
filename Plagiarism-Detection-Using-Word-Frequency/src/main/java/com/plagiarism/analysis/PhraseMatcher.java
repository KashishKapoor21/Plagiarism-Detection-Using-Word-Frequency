package com.plagiarism.analysis;

import java.util.HashSet;
import java.util.Set;

public class PhraseMatcher {

    public Set<String> findCommonPhrases(String text1, String text2) {

        return findCommonPhrases(text1, text2, 3);
    }

    public Set<String> findCommonPhrases(
            String text1,
            String text2,
            int n) {

        if (n < 1) {
            throw new IllegalArgumentException(
                    "Phrase length must be at least 1"
            );
        }

        Set<String> phrases1 = generateNGrams(text1, n);
        Set<String> phrases2 = generateNGrams(text2, n);

        phrases1.retainAll(phrases2);

        return phrases1;
    }

    private Set<String> generateNGrams(String text, int n) {

        Set<String> ngrams = new HashSet<>();

        if (text == null || text.isBlank()) {
            return ngrams;
        }

        // Split text into sentences
        String[] sentences = text.split("[.!?\\n]+");

        for (String sentence : sentences) {

            sentence = sentence.toLowerCase();

            sentence = sentence.replaceAll("[^a-z0-9\\s]", "");

            sentence = sentence.replaceAll("\\s+", " ").trim();

            if (sentence.isBlank()) {
                continue;
            }

            String[] words = sentence.split("\\s+");

            for (int i = 0; i <= words.length - n; i++) {

                StringBuilder phrase = new StringBuilder();

                for (int j = 0; j < n; j++) {

                    if (j > 0) {
                        phrase.append(" ");
                    }

                    phrase.append(words[i + j]);
                }

                ngrams.add(phrase.toString());
            }
        }

        return ngrams;
    }
}