package com.plagiarism.analysis;

import java.util.HashMap;
import java.util.Map;

public class WordFrequency {

    public Map<String, Integer> calculateFrequency(String text) {

        Map<String, Integer> frequencyMap = new HashMap<>();

        if (text == null || text.isBlank()) {
            return frequencyMap;
        }

        String[] words = text.split("\\s+");

        for (String word : words) {

            if (!word.isBlank()) {

                frequencyMap.put(
                        word,
                        frequencyMap.getOrDefault(word, 0) + 1
                );
            }
        }

        return frequencyMap;
    }

    public int getTotalWordCount(Map<String, Integer> frequencyMap) {

        int total = 0;

        for (int count : frequencyMap.values()) {
            total += count;
        }

        return total;
    }

    public int getUniqueWordCount(Map<String, Integer> frequencyMap) {

        return frequencyMap.size();
    }
}