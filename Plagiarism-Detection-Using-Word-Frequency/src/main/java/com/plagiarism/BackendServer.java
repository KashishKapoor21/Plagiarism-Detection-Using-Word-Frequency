package com.plagiarism;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plagiarism.preprocessing.TextPreprocessor;
import com.plagiarism.analysis.WordFrequency;
import com.plagiarism.analysis.PhraseMatcher;
import com.plagiarism.analysis.SimilarityChecker;
import com.plagiarism.analysis.SimilarityClassifier;
import com.plagiarism.database.DocumentDAO;
import com.plagiarism.database.ComparisonDAO;
import com.plagiarism.database.PhraseDAO;
import com.plagiarism.database.ReportDAO;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BackendServer {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final int MAX_REQUEST_SIZE = 5 * 1024 * 1024;

    private static final TextPreprocessor preprocessor =
            new TextPreprocessor();

    private static final WordFrequency wordFrequency =
            new WordFrequency();

    private static final PhraseMatcher phraseMatcher =
            new PhraseMatcher();

    private static final SimilarityChecker similarityChecker =
            new SimilarityChecker();

    private static final SimilarityClassifier classifier =
            new SimilarityClassifier();

    // DATABASE DAO OBJECTS
    private static final DocumentDAO documentDAO =
            new DocumentDAO();

    private static final ComparisonDAO comparisonDAO =
            new ComparisonDAO();

    private static final PhraseDAO phraseDAO =
            new PhraseDAO();


    // START SERVER
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        server.createContext(
                "/api/compare",
                BackendServer::compareDocuments
        );

        server.createContext(
                "/api/health",
                BackendServer::healthCheck
        );

        server.createContext(
                "/api/reports",
                BackendServer::getReports
        );

        server.start();

        System.out.println("========================================");
        System.out.println(" PLAGIARISM DETECTION BACKEND");
        System.out.println("========================================");
        System.out.println("Server running at:");
        System.out.println("http://localhost:8080");
        System.out.println();
        System.out.println("Health endpoint:");
        System.out.println("http://localhost:8080/api/health");
        System.out.println();
        System.out.println("Waiting for frontend requests...");
    }


    // HEALTH CHECK
    private static void healthCheck(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        try {

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                sendResponse(
                        exchange,
                        405,
                        Map.of("error", "Method not allowed")
                );

                return;
            }

            Map<String, Object> response = new HashMap<>();

            response.put("status", "running");
            response.put("message", "Java backend is working!");

            sendResponse(exchange, 200, response);

        } finally {
            exchange.close();
        }
    }


    // GET REPORT HISTORY
    private static void getReports(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        try {

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                sendResponse(
                        exchange,
                        405,
                        Map.of("error", "Only GET requests are allowed.")
                );

                return;
            }

            try {

                ReportDAO reportDAO = new ReportDAO();

                List<Map<String, Object>> reports =
                        reportDAO.getAllReports();

                Map<String, Object> response = new HashMap<>();

                response.put("success", true);
                response.put("reports", reports);
                response.put("totalReports", reports.size());

                sendResponse(exchange, 200, response);

            } catch (Exception e) {

                System.err.println("Report retrieval failed:");
                e.printStackTrace();

                sendResponse(
                        exchange,
                        500,
                        Map.of(
                                "success", false,
                                "error", "Failed to retrieve reports."
                        )
                );
            }

        } finally {
            exchange.close();
        }
    }


    // COMPARE DOCUMENTS
    private static void compareDocuments(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        try {

            // Browser preflight request
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // POST only
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                sendResponse(
                        exchange,
                        405,
                        Map.of("error", "Only POST requests are allowed.")
                );

                return;
            }

            // REQUEST SIZE VALIDATION
            String contentLengthHeader =
                    exchange.getRequestHeaders().getFirst("Content-Length");

            if (contentLengthHeader != null) {

                try {

                    long contentLength =
                            Long.parseLong(contentLengthHeader);

                    if (contentLength > MAX_REQUEST_SIZE) {

                        sendResponse(
                                exchange,
                                413,
                                Map.of("error", "Request is too large.")
                        );

                        return;
                    }

                } catch (NumberFormatException e) {

                    sendResponse(
                            exchange,
                            400,
                            Map.of("error", "Invalid Content-Length header.")
                    );

                    return;
                }
            }

            // READ REQUEST BODY
            byte[] requestBytes =
                    exchange.getRequestBody().readNBytes(
                            MAX_REQUEST_SIZE + 1
                    );

            if (requestBytes.length > MAX_REQUEST_SIZE) {

                sendResponse(
                        exchange,
                        413,
                        Map.of("error", "Request is too large.")
                );

                return;
            }

            String requestBody = new String(
                    requestBytes,
                    StandardCharsets.UTF_8
            );

            // EMPTY REQUEST VALIDATION
            if (requestBody.isBlank()) {

                sendResponse(
                        exchange,
                        400,
                        Map.of("error", "Request body cannot be empty.")
                );

                return;
            }

            JsonNode root;

            // JSON VALIDATION
            try {

                root = mapper.readTree(requestBody);

            } catch (Exception e) {

                sendResponse(
                        exchange,
                        400,
                        Map.of("error", "Invalid JSON request.")
                );

                return;
            }

            if (root == null || !root.isObject()) {

                sendResponse(
                        exchange,
                        400,
                        Map.of("error", "Request must be a JSON object.")
                );

                return;
            }

            JsonNode documentsNode = root.get("documents");

            if (documentsNode == null || !documentsNode.isArray()) {

                sendResponse(
                        exchange,
                        400,
                        Map.of("error", "Invalid documents input.")
                );

                return;
            }

            if (documentsNode.size() < 2) {

                sendResponse(
                        exchange,
                        400,
                        Map.of("error", "At least two documents are required.")
                );

                return;
            }

            // READ AND VALIDATE DOCUMENTS
            List<DocumentData> documents = new ArrayList<>();

            for (JsonNode documentNode : documentsNode) {

                // Validate document object
                if (documentNode == null || !documentNode.isObject()) {

                    sendResponse(
                            exchange,
                            400,
                            Map.of("error", "Each document must be a valid JSON object.")
                    );

                    return;
                }

                JsonNode nameNode = documentNode.get("name");
                JsonNode contentNode = documentNode.get("content");

                // Validate document name
                if (nameNode == null || !nameNode.isTextual()
                        || nameNode.asText().isBlank()) {

                    sendResponse(
                            exchange,
                            400,
                            Map.of("error", "Document name cannot be empty.")
                    );

                    return;
                }

                // Validate document content
                if (contentNode == null || !contentNode.isTextual()
                        || contentNode.asText().isBlank()) {

                    sendResponse(
                            exchange,
                            400,
                            Map.of("error", "Document content cannot be empty.")
                    );

                    return;
                }

                String name = nameNode.asText().trim();
                String content = contentNode.asText();

                String cleanText =
                        preprocessor.normalizeText(content);

                Map<String, Integer> frequency =
                        wordFrequency.calculateFrequency(cleanText);

                int totalWords = getWordCount(cleanText);

                int uniqueWords = frequency.size();

                // SAVE DOCUMENT
                int documentId = documentDAO.saveDocument(
                        name,
                        content,
                        totalWords,
                        uniqueWords
                );

                documents.add(
                        new DocumentData(
                                name,
                                content,
                                cleanText,
                                frequency,
                                documentId
                        )
                );

                System.out.println("Document saved: " + name);
            }


            // COMPARE EVERY PAIR
            List<Map<String, Object>> results =
                    new ArrayList<>();

            for (int i = 0; i < documents.size() - 1; i++) {

                for (int j = i + 1; j < documents.size(); j++) {

                    DocumentData doc1 = documents.get(i);
                    DocumentData doc2 = documents.get(j);

                    Map<String, Object> result =
                            analyzePair(doc1, doc2);

                    results.add(result);
                }
            }


            // RESPONSE
            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put("comparisons", results);
            response.put("totalDocuments", documents.size());
            response.put("totalComparisons", results.size());

            sendResponse(exchange, 200, response);

        } catch (Exception e) {

            System.err.println("Document analysis failed:");
            e.printStackTrace();

            try {

                sendResponse(
                        exchange,
                        500,
                        Map.of(
                                "success", false,
                                "error", "An internal error occurred during analysis."
                        )
                );

            } catch (IOException ignored) {
                System.err.println("Could not send error response.");
            }

        } finally {
            exchange.close();
        }
    }


    // ANALYZE DOCUMENT PAIR
    private static Map<String, Object> analyzePair(
            DocumentData doc1,
            DocumentData doc2
    ) {

        double similarity =
                similarityChecker.calculateSimilarity(
                        doc1.frequency,
                        doc2.frequency
                );

        String classification =
                classifier.classify(similarity);


        // COMMON WORDS
        Set<String> commonWords =
                new TreeSet<>(doc1.frequency.keySet());

        commonWords.retainAll(doc2.frequency.keySet());


        // COMMON PHRASES
        Set<String> commonPhrases =
                phraseMatcher.findCommonPhrases(
                        doc1.originalText,
                        doc2.originalText
                );


        // IDENTICAL CHECK
        boolean identical =
                doc1.text.equals(doc2.text);


        // SAVE COMPARISON
        int comparisonId = comparisonDAO.saveComparison(
                doc1.documentId,
                doc2.documentId,
                similarity,
                classification,
                identical
        );


        // SAVE COMMON PHRASES
        if (comparisonId != -1) {

            phraseDAO.savePhrases(
                    comparisonId,
                    commonPhrases
            );
        }


        // RESULT MAP
        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("document1", doc1.name);
        result.put("document2", doc2.name);

        result.put("similarity", similarity);
        result.put("classification", classification);

        result.put("totalWords1", getWordCount(doc1.text));
        result.put("totalWords2", getWordCount(doc2.text));

        result.put("uniqueWords1", doc1.frequency.size());
        result.put("uniqueWords2", doc2.frequency.size());

        result.put("frequency1", doc1.frequency);
        result.put("frequency2", doc2.frequency);

        result.put("commonWords", commonWords);
        result.put("commonPhrases", commonPhrases);

        result.put("identical", identical);

        return result;
    }


    // WORD COUNT
    private static int getWordCount(String text) {

        if (text == null || text.isBlank()) {
            return 0;
        }

        return text.trim().split("\\s+").length;
    }


    // CORS
    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }


    // SEND JSON RESPONSE
    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            Object response
    ) throws IOException {

        byte[] responseBytes =
                mapper.writeValueAsBytes(response);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        exchange.getResponseBody().write(responseBytes);
        exchange.getResponseBody().close();
    }


    // DOCUMENT DATA
    static class DocumentData {

        String name;
        String originalText;
        String text;

        Map<String, Integer> frequency;

        int documentId;

        DocumentData(
                String name,
                String originalText,
                String text,
                Map<String, Integer> frequency,
                int documentId
        ) {

            this.name = name;
            this.originalText = originalText;
            this.text = text;
            this.frequency = frequency;
            this.documentId = documentId;
        }
    }
}