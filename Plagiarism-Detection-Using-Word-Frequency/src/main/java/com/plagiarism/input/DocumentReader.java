package com.plagiarism.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DocumentReader {

    public String readFile(String filePath) throws IOException {

        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + filePath);
        }

        if (!Files.isRegularFile(path)) {
            throw new IOException("Path is not a valid file.");
        }

        String content = Files.readString(path);

        if (content.isBlank()) {
            throw new IOException("File is empty: " + filePath);
        }

        return content;
    }
}