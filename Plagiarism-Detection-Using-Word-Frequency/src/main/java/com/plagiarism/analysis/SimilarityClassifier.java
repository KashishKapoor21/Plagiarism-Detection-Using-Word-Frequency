package com.plagiarism.analysis;

public class SimilarityClassifier {

    public String classify(double similarity) {

        if (similarity <= 20) {
            return "LOW";
        }

        else if (similarity <= 50) {
            return "MODERATE";
        }

        else if (similarity <= 75) {
            return "HIGH";
        }

        else {
            return "VERY HIGH";
        }
    }
}