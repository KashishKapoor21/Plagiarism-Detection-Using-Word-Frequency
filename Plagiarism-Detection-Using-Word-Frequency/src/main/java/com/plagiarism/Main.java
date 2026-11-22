package com.plagiarism;

import com.plagiarism.input.DocumentReader;
import com.plagiarism.preprocessing.TextPreprocessor;
import com.plagiarism.analysis.WordFrequency;
import com.plagiarism.analysis.PhraseMatcher;
import com.plagiarism.analysis.SimilarityChecker;
import com.plagiarism.analysis.SimilarityClassifier;
import com.plagiarism.report.ReportGenerator;

import java.io.IOException;
import java.util.*;

public class Main {

    static class DocumentData {

        String name;
        String originalText;
        String text;
        Map<String, Integer> frequency;

        DocumentData(
                String name,
                String originalText,
                String text,
                Map<String, Integer> frequency) {

            this.name = name;

            this.originalText = originalText;

            this.text = text;

            this.frequency = frequency;
        }
    }

    public static void main(String[] args) {
        DatabaseConnection.testConnection();
        System.out.println("========================================");
        System.out.println("     PLAGIARISM DETECTION SYSTEM");
        System.out.println("========================================");
        System.out.println("          TEAM TECH TASHANS");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        DocumentReader reader = new DocumentReader();

        TextPreprocessor preprocessor = new TextPreprocessor();

        WordFrequency wordFrequency = new WordFrequency();

        PhraseMatcher phraseMatcher = new PhraseMatcher();

        SimilarityChecker similarityChecker = new SimilarityChecker();

        SimilarityClassifier similarityClassifier =
                new SimilarityClassifier();

        ReportGenerator reportGenerator = new ReportGenerator();

        List<DocumentData> documents = new ArrayList<>();

        // STEP 1: Input document count

        System.out.print("Enter number of documents: ");

        int count;

        try {

            count = Integer.parseInt(sc.nextLine());

            if (count < 2) {

                System.out.println(
                        "Minimum 2 documents are required."
                );

                return;
            }

        } catch (NumberFormatException e) {

            System.out.println("Invalid number entered.");

            return;
        }

        // STEP 2: Read documents

        for (int i = 1; i <= count; i++) {

            System.out.print(
                    "Enter path of Document " + i + ": "
            );

            String path = sc.nextLine().trim();

            try {

                String originalText = reader.readFile(path);

                String cleanText =
                        preprocessor.normalizeText(originalText);

                Map<String, Integer> frequency =
                        wordFrequency.calculateFrequency(cleanText);

                DocumentData document = new DocumentData(
                        "Document " + i,
                        originalText,
                        cleanText,
                        frequency
                );

                documents.add(document);

                System.out.println(
                        "Document loaded successfully!"
                );

                System.out.println(
                        "Total words: " +
                                wordFrequency.getTotalWordCount(frequency)
                );

                System.out.println(
                        "Unique words: " +
                                wordFrequency.getUniqueWordCount(frequency)
                );

            } catch (IOException e) {

                System.out.println(
                        "Error loading document: " + e.getMessage()
                );
            }
        }

        // STEP 3: Check if enough valid documents exist

        if (documents.size() < 2) {

            System.out.println(
                    "Not enough valid documents to compare."
            );

            return;
        }

        // STEP 4: Compare documents

        System.out.println("\n========================================");

        System.out.println("       COMPARISON RESULTS");

        System.out.println("========================================");

        for (int i = 0; i < documents.size() - 1; i++) {

            for (int j = i + 1; j < documents.size(); j++) {

                DocumentData doc1 = documents.get(i);

                DocumentData doc2 = documents.get(j);

                System.out.println(
                        "\nComparing " +
                                doc1.name + " and " + doc2.name
                );

                // Duplicate check

                if (doc1.text.equals(doc2.text)) {

                    System.out.println(
                            "WARNING: Documents are identical!"
                    );
                }

                // Calculate similarity

                double similarity =
                        similarityChecker.calculateSimilarity(
                                doc1.frequency,
                                doc2.frequency
                        );

                String classification =
                        similarityClassifier.classify(similarity);

                // Common words

                Set<String> commonWords =
                        new TreeSet<>(doc1.frequency.keySet());

                commonWords.retainAll(doc2.frequency.keySet());

                // Common phrases

                Set<String> commonPhrases =
                        phraseMatcher.findCommonPhrases(
                                doc1.originalText,
                                doc2.originalText
                        );

                // Word frequencies

                Map<String, Integer> sortedFrequency1 =
                        new TreeMap<>(doc1.frequency);

                Map<String, Integer> sortedFrequency2 =
                        new TreeMap<>(doc2.frequency);

                // Word counts

                int totalWords1 =
                        wordFrequency.getTotalWordCount(
                                doc1.frequency
                        );

                int totalWords2 =
                        wordFrequency.getTotalWordCount(
                                doc2.frequency
                        );

                int uniqueWords1 =
                        wordFrequency.getUniqueWordCount(
                                doc1.frequency
                        );

                int uniqueWords2 =
                        wordFrequency.getUniqueWordCount(
                                doc2.frequency
                        );

                // STEP 5: Generate report

                StringBuilder report = new StringBuilder();

                report.append(
                        "========================================\n"
                );

                report.append(
                        "      PLAGIARISM DETECTION REPORT\n"
                );

                report.append(
                        "========================================\n"
                );

                report.append("\nComparing: ");

                report.append(doc1.name);

                report.append(" and ");

                report.append(doc2.name);

                // Document statistics

                report.append("\n\nDOCUMENT STATISTICS");

                report.append("\n----------------------------------------");

                report.append("\n");

                report.append(doc1.name);

                report.append("\nTotal Words: ");

                report.append(totalWords1);

                report.append("\nUnique Words: ");

                report.append(uniqueWords1);

                report.append("\n");

                report.append(doc2.name);

                report.append("\nTotal Words: ");

                report.append(totalWords2);

                report.append("\nUnique Words: ");

                report.append(uniqueWords2);

                // Word frequency

                report.append("\n\nWORD FREQUENCY ANALYSIS");

                report.append("\n----------------------------------------");

                report.append("\n");

                report.append(doc1.name);

                report.append(": ");

                report.append(sortedFrequency1);

                report.append("\n");

                report.append(doc2.name);

                report.append(": ");

                report.append(sortedFrequency2);

                // Common words

                report.append("\n\nCOMMON WORDS");

                report.append("\n----------------------------------------");

                report.append("\n");

                report.append(commonWords);

                // Common phrases

                report.append("\n\nCOMMON THREE-WORD PHRASES");

                report.append("\n----------------------------------------");

                report.append("\n");

                if (commonPhrases.isEmpty()) {

                    report.append("No common phrases found.");

                } else {

                    report.append(commonPhrases);
                }

                // Similarity

                report.append("\n\nSIMILARITY ANALYSIS");

                report.append("\n----------------------------------------");

                report.append(
                        String.format(
                                Locale.ROOT,
                                "\nSimilarity Score: %.2f%%",
                                similarity
                        )
                );

                report.append("\nClassification: ");

                report.append(classification);

                // Duplicate detection

                report.append("\nIdentical Documents: ");

                report.append(
                        doc1.text.equals(doc2.text)
                                ? "YES"
                                : "NO"
                );

                report.append("\n\n");

                report.append(
                        "NOTE: Similarity score is based on word frequency. "
                );

                report.append(
                        "It does not prove plagiarism."
                );

                report.append(
                        "\n========================================"
                );

                // STEP 6: Display report

                System.out.println(report);

                // STEP 7: Save report

                String reportName =
                        "comparison_" +
                                (i + 1) +
                                "_" +
                                (j + 1) +
                                ".txt";

                try {

                    reportGenerator.generateReport(
                            reportName,
                            report.toString()
                    );

                } catch (IOException e) {

                    System.out.println(
                            "Failed to save report: " +
                                    e.getMessage()
                    );
                }
            }
        }

        // STEP 8: Completion

        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "ALL COMPARISONS COMPLETED SUCCESSFULLY"
        );

        System.out.println(
                "========================================"
        );

        sc.close();
    }
}