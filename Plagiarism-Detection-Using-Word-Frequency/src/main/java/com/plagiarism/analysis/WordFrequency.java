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

        if (frequencyMap == null || frequencyMap.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (int count : frequencyMap.values()) {
            total += count;
        }

        return total;
    }

    public int getUniqueWordCount(Map<String, Integer> frequencyMap) {

        if (frequencyMap == null) {
            return 0;
        }

        return frequencyMap.size();
    }

    public String getMostFrequentWord(Map<String, Integer> frequencyMap) {

        if (frequencyMap == null || frequencyMap.isEmpty()) {
            return null;
        }

        String mostFrequentWord = null;
        int highestFrequency = 0;

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {

            if (entry.getValue() > highestFrequency) {

                highestFrequency = entry.getValue();
                mostFrequentWord = entry.getKey();
            }
        }

        return mostFrequentWord;
    }

    public double getFrequencyPercentage(
            Map<String, Integer> frequencyMap,
            String word) {

        if (frequencyMap == null || frequencyMap.isEmpty()
                || word == null || !frequencyMap.containsKey(word)) {
            return 0.0;
        }

        int totalWords = getTotalWordCount(frequencyMap);

        if (totalWords == 0) {
            return 0.0;
        }

        int wordFrequency = frequencyMap.get(word);

        return (wordFrequency * 100.0) / totalWords;
    }
}