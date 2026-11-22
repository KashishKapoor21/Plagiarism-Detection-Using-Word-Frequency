package com.plagiarism.database;

import com.plagiarism.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ComparisonDAO {

    public int saveComparison(
            int document1Id,
            int document2Id,
            double similarityScore,
            String classification,
            boolean identical
    ) {

        String query = """
                INSERT INTO comparisons
                (document1_id, document2_id, similarity_score,
                 classification, identical_documents)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             query,
                             PreparedStatement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(1, document1Id);
            statement.setInt(2, document2Id);
            statement.setDouble(3, similarityScore);
            statement.setString(4, classification);
            statement.setBoolean(5, identical);

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Comparison saving failed: " + e.getMessage()
            );
        }

        return -1;
    }
}