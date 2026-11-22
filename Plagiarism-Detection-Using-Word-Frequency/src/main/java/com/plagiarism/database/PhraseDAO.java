package com.plagiarism.database;

import com.plagiarism.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class PhraseDAO {

    public void savePhrases(int comparisonId, Collection<String> phrases) {

        if (phrases == null || phrases.isEmpty()) {
            return;
        }

        String query = """
                INSERT INTO common_phrases (comparison_id, phrase_text)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (String phrase : phrases) {

                statement.setInt(1, comparisonId);
                statement.setString(2, phrase);

                statement.addBatch();
            }

            statement.executeBatch();

            System.out.println("Common phrases saved successfully!");

        } catch (SQLException e) {

            System.out.println("Phrase saving failed: " + e.getMessage());
        }
    }
}