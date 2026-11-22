package com.plagiarism.database;

import com.plagiarism.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public List<Map<String, Object>> getAllReports() {

        List<Map<String, Object>> reports = new ArrayList<>();

        String query = """
                SELECT
                    c.comparison_id,
                    d1.document_name AS document1,
                    d2.document_name AS document2,
                    c.similarity_score,
                    c.classification,
                    c.identical_documents,
                    c.comparison_date
                FROM comparisons c
                JOIN documents d1
                    ON c.document1_id = d1.document_id
                JOIN documents d2
                    ON c.document2_id = d2.document_id
                ORDER BY c.comparison_date DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Map<String, Object> report =
                        new LinkedHashMap<>();

                report.put(
                        "comparisonId",
                        resultSet.getInt("comparison_id")
                );

                report.put(
                        "document1",
                        resultSet.getString("document1")
                );

                report.put(
                        "document2",
                        resultSet.getString("document2")
                );

                report.put(
                        "similarity",
                        resultSet.getDouble("similarity_score")
                );

                report.put(
                        "classification",
                        resultSet.getString("classification")
                );

                report.put(
                        "identical",
                        resultSet.getBoolean("identical_documents")
                );

                report.put(
                        "comparisonDate",
                        resultSet.getTimestamp("comparison_date").toString()
                );

                reports.add(report);
            }

        } catch (SQLException e) {

            System.out.println("Report retrieval failed: " + e.getMessage());
        }

        return reports;
    }
}