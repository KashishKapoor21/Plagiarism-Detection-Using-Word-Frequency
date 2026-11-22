# DOPPEL
## Plagiarism Detection Using Word Frequency

DOPPEL is a Java-based plagiarism detection system that analyzes the similarity between documents using word frequency analysis, cosine similarity, and common phrase detection.

The system preprocesses text, compares documents mathematically, classifies similarity levels, and generates comparison reports.

---

## Features

- Upload and read text documents
- Text preprocessing and normalization
- Word frequency analysis
- Cosine similarity calculation
- Common phrase detection using 3-word phrases
- Similarity classification
- Comparison report generation
- REST API backend
- MySQL database integration
- Sample documents for testing

---

## Technologies Used

- Java 26
- Maven
- MySQL
- JDBC
- HTTP Server
- JSON

---

## Project Structure

\`\`\`
Plagiarism-Detection-Using-Word-Frequency/
│
├── src/main/java/com/plagiarism/
│   ├── BackendServer.java
│   ├── DatabaseConnection.java
│   ├── Main.java
│   │
│   ├── analysis/
│   │   ├── WordFrequency.java
│   │   ├── SimilarityChecker.java
│   │   ├── SimilarityClassifier.java
│   │   └── PhraseMatcher.java
│   │
│   ├── database/
│   │   ├── ComparisonDAO.java
│   │   ├── DocumentDAO.java
│   │   ├── PhraseDAO.java
│   │   └── ReportDAO.java
│   │
│   ├── input/
│   │   └── DocumentReader.java
│   │
│   ├── preprocessing/
│   │   └── TextPreprocessor.java
│   │
│   └── report/
│       └── ReportGenerator.java
│
├── Test_Documents/
│   ├── document1.txt
│   ├── document2.txt
│   └── document3.txt
│
├── frontend/
├── pom.xml
└── README.md
\`\`\`

---

## How It Works

### 1. Document Input
The system accepts text documents for comparison.

### 2. Text Preprocessing
Text is cleaned and normalized before analysis.

### 3. Word Frequency Analysis
The frequency of individual words is calculated for each document.

### 4. Similarity Calculation
Cosine similarity is used to calculate the similarity between document word-frequency vectors.

### 5. Phrase Matching
Common three-word phrases are identified to detect matching sequences of text.

### 6. Similarity Classification
The calculated similarity is categorized into different plagiarism similarity levels.

### 7. Report Generation
The system generates comparison reports containing the analysis results.

---

## Backend API

The backend runs on:

\`\`\`
http://localhost:8080
\`\`\`

### Compare Documents

**Endpoint:**

\`\`\`
POST /api/compare
\`\`\`

Used to submit documents for comparison and retrieve similarity analysis.

### Retrieve Reports

**Endpoint:**

\`\`\`
GET /api/reports
\`\`\`

Used to retrieve previously generated comparison reports.

---

## Database Setup

The project uses MySQL.

### 1. Install MySQL

Ensure MySQL Server is installed and running.

### 2. Create the Database

\`\`\`sql
CREATE DATABASE plagiarism_detector;
\`\`\`

### 3. Configure Environment Variables

Set the following environment variables:

| Variable | Description |
|---|---|
| DB_URL | MySQL database connection URL |
| DB_USER | MySQL username |
| DB_PASSWORD | MySQL password |

Default database URL:

\`\`\`
jdbc:mysql://localhost:3306/plagiarism_detector
\`\`\`

The database password is configured through environment variables and is not stored in the repository.

---

## Running the Project

### Prerequisites

- JDK 26 or compatible Java version
- Maven
- MySQL Server
- Git

### 1. Clone the Repository

\`\`\`bash
git clone https://github.com/KashishKapoor21/Plagiarism-Detection-Using-Word-Frequency.git
\`\`\`

### 2. Open the Project

Open the cloned project in IntelliJ IDEA.

### 3. Configure Database

Create the `plagiarism_detector` database and configure your database environment variables.

### 4. Install Dependencies

Run:

\`\`\`bash
mvn clean install
\`\`\`

### 5. Run the Backend

Run the backend server through IntelliJ or execute the configured Java entry point.

The backend server should start on port `8080`.

---

## Team Project

This project was developed collaboratively as an academic software project.

---

## Disclaimer

This system provides similarity analysis to assist in identifying potentially plagiarized content. Similarity scores alone do not conclusively establish plagiarism and should be interpreted alongside contextual analysis.