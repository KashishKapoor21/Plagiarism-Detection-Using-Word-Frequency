package com.plagiarism.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportGenerator {

    public void generateReport(
            String fileName,
            String reportContent) throws IOException {

        Path reportDirectory = Path.of("reports");

        Files.createDirectories(reportDirectory);

        Path reportPath = reportDirectory.resolve(fileName);

        Files.writeString(reportPath, reportContent);

        System.out.println("\nReport generated successfully!");

        System.out.println(
                "Location: " + reportPath.toAbsolutePath()
        );
    }
}