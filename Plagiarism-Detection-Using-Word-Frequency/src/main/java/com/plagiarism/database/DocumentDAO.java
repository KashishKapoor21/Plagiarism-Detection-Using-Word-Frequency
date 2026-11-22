package com.plagiarism.database;

import com.plagiarism.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DocumentDAO {

    public int saveDocument(
            String documentName,
            String documentContent,
            int totalWords,
            int uniqueWords
    ) {

        String query = """
                INSERT INTO documents
                (document_name, document_content, total_words, unique_words)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     query,
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {

            statement.setString(1, documentName);
            statement.setString(2, documentContent);
            statement.setInt(3, totalWords);
            statement.setInt(4, uniqueWords);

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Document saving failed: " + e.getMessage());
        }

        return -1;
    }
}