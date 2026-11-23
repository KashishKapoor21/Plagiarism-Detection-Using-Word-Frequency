// ========================================
// DOM ELEMENTS
// ========================================

const fileInput = document.getElementById("fileInput");
const browseButton = document.getElementById("browseButton");
const uploadBox = document.getElementById("uploadBox");

const documentList = document.getElementById("documentList");
const documentCount = document.getElementById("documentCount");

const compareButton = document.getElementById("compareButton");

const totalDocuments = document.getElementById("totalDocuments");
const totalComparisons = document.getElementById("totalComparisons");
const highSimilarity = document.getElementById("highSimilarity");

const resultsContainer = document.getElementById("resultsContainer");
const downloadReportButton =
    document.getElementById("downloadReportButton");

const reportsContainer =
    document.getElementById("reportsContainer");

const refreshReportsButton =
    document.getElementById("refreshReportsButton");

const homeNav = document.getElementById("homeNav");
const dashboardNav = document.getElementById("dashboardNav");
const reportsNav = document.getElementById("reportsNav");

const historyTotal = document.getElementById("historyTotal");
const historyHighest = document.getElementById("historyHighest");
const historyAverage = document.getElementById("historyAverage");
const latestComparison = document.getElementById("latestComparison");

const filterButtons =
    document.querySelectorAll(".filter-button");


// ========================================
// STORAGE
// ========================================

let uploadedDocuments = [];
let comparisonResults = [];
let reportHistory = [];

let currentFilter = "all";

const BACKEND_URL = "http://localhost:8080/api/compare";
const REPORTS_URL = "http://localhost:8080/api/reports";


// ========================================
// FILE BROWSE
// ========================================

browseButton.addEventListener("click", () => {
    fileInput.click();
});

fileInput.addEventListener("change", (event) => {

    handleFiles(event.target.files);

    fileInput.value = "";
});


// ========================================
// DRAG AND DROP
// ========================================

uploadBox.addEventListener("dragover", (event) => {

    event.preventDefault();

    uploadBox.classList.add("drag-active");
});

uploadBox.addEventListener("dragleave", () => {

    uploadBox.classList.remove("drag-active");
});

uploadBox.addEventListener("drop", (event) => {

    event.preventDefault();

    uploadBox.classList.remove("drag-active");

    handleFiles(event.dataTransfer.files);
});


// ========================================
// HANDLE FILES
// ========================================

function handleFiles(files) {

    for (const file of files) {

        if (!file.name.toLowerCase().endsWith(".txt")) {

            alert(`${file.name} is not a TXT file.`);

            continue;
        }

        const alreadyExists = uploadedDocuments.some(
            doc => doc.name === file.name
        );

        if (alreadyExists) {

            alert(`${file.name} is already uploaded.`);

            continue;
        }

        uploadedDocuments.push(file);
    }

    updateDocumentList();
    updateStatistics();

    comparisonResults = [];

    resetResults();
}


// ========================================
// DISPLAY DOCUMENTS
// ========================================

function updateDocumentList() {

    documentList.innerHTML = "";

    documentCount.textContent =
        `${uploadedDocuments.length} files`;

    if (uploadedDocuments.length === 0) {

        documentList.innerHTML = `
            <div class="empty-state">
                No documents uploaded yet.
            </div>
        `;

        compareButton.disabled = true;

        return;
    }

    uploadedDocuments.forEach((file, index) => {

        const item = document.createElement("div");

        item.className = "document-item";

        item.innerHTML = `
            <div class="document-info">

                <div class="document-icon">
                    TXT
                </div>

                <div>

                    <div class="document-name">
                        ${escapeHTML(file.name)}
                    </div>

                    <div class="document-size">
                        ${formatFileSize(file.size)}
                    </div>

                </div>

            </div>

            <button
                class="remove-button"
                data-index="${index}"
            >
                Remove
            </button>
        `;

        documentList.appendChild(item);
    });

    compareButton.disabled =
        uploadedDocuments.length < 2;
}


// ========================================
// REMOVE DOCUMENT
// ========================================

documentList.addEventListener("click", (event) => {

    const removeButton =
        event.target.closest(".remove-button");

    if (!removeButton) {
        return;
    }

    const index = Number(removeButton.dataset.index);

    if (
        Number.isNaN(index) ||
        index < 0 ||
        index >= uploadedDocuments.length
    ) {
        return;
    }

    uploadedDocuments.splice(index, 1);

    updateDocumentList();
    updateStatistics();

    comparisonResults = [];

    resetResults();
});


// ========================================
// RESET RESULTS
// ========================================

function resetResults() {

    resultsContainer.innerHTML = `
        <div class="empty-state">
            Upload and compare documents to view results.
        </div>
    `;

    downloadReportButton.disabled = true;
}


// ========================================
// UPDATE DASHBOARD STATISTICS
// ========================================

function updateStatistics() {

    totalDocuments.textContent =
        uploadedDocuments.length;

    totalComparisons.textContent =
        reportHistory.length;

    highSimilarity.textContent =
        reportHistory.filter(report =>
            report.classification === "HIGH" ||
            report.classification === "VERY HIGH"
        ).length;
}


// ========================================
// FILE SIZE FORMATTER
// ========================================

function formatFileSize(bytes) {

    if (bytes < 1024) {
        return bytes + " Bytes";
    }

    if (bytes < 1024 * 1024) {
        return (bytes / 1024).toFixed(2) + " KB";
    }

    return (bytes / (1024 * 1024)).toFixed(2) + " MB";
}


// ========================================
// HTML ESCAPING
// ========================================

function escapeHTML(text) {

    const div = document.createElement("div");

    div.textContent = text ?? "";

    return div.innerHTML;
}


// ========================================
// SAFE SIMILARITY VALUE
// ========================================

function getSimilarityValue(value) {

    const similarity = Number(value);

    if (!Number.isFinite(similarity)) {
        return 0;
    }

    return Math.max(0, Math.min(similarity, 100));
}


// ========================================
// CLASSIFICATION COLORS
// ========================================

function getClassificationColor(classification) {

    switch (classification) {

        case "VERY HIGH":
            return "#D3968C";

        case "HIGH":
            return "#B98572";

        case "MODERATE":
            return "#839958";

        case "LOW":
            return "#105666";

        default:
            return "#839958";
    }
}


// ========================================
// JAVA BACKEND INTEGRATION
// ========================================

async function compareDocumentsWithJava() {

    const documents = await Promise.all(

        uploadedDocuments.map(async (file) => {

            const content = await file.text();

            return {
                name: file.name,
                content: content
            };
        })
    );

    const response = await fetch(BACKEND_URL, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            documents: documents
        })
    });

    if (!response.ok) {

        let errorData = {};

        try {
            errorData = await response.json();
        } catch {
            // Response wasn't JSON
        }

        throw new Error(
            errorData.error ||
            `Backend error: ${response.status}`
        );
    }

    const data = await response.json();

    if (!data.success) {
        throw new Error("Java analysis failed.");
    }

    if (!Array.isArray(data.comparisons)) {
        throw new Error("Invalid comparison response from backend.");
    }

    return data.comparisons;
}


// ========================================
// COMPARE DOCUMENTS
// ========================================

compareButton.addEventListener("click", async () => {

    if (uploadedDocuments.length < 2) {

        alert("Upload at least two documents.");

        return;
    }

    compareButton.disabled = true;

    downloadReportButton.disabled = true;

    compareButton.textContent = "Analyzing...";

    resultsContainer.innerHTML = `
        <div class="empty-state">
            Sending documents to Java backend...
            <br><br>
            Analyzing documents. Please wait.
        </div>
    `;

    comparisonResults = [];

    try {

        comparisonResults =
            await compareDocumentsWithJava();

        displayResults();

        downloadReportButton.disabled =
            comparisonResults.length === 0;

        await loadReports();

        updateStatistics();

    } catch (error) {

        console.error("Backend error:", error);

        resultsContainer.innerHTML = `
            <div class="empty-state">
                <strong>Analysis Failed</strong>
                <br><br>
                ${escapeHTML(error.message)}
                <br><br>
                Make sure the Java backend is running
                at localhost:8080.
            </div>
        `;

    } finally {

        compareButton.disabled =
            uploadedDocuments.length < 2;

        compareButton.textContent =
            "Compare documents";
    }
});


// ========================================
// DISPLAY RESULTS
// ========================================

function displayResults() {

    resultsContainer.innerHTML = "";

    if (comparisonResults.length === 0) {

        resultsContainer.innerHTML = `
            <div class="empty-state">
                No comparisons available.
            </div>
        `;

        return;
    }

    comparisonResults.forEach((result, index) => {

        const card = document.createElement("div");

        card.className = "result-card";

        const classificationColor =
            getClassificationColor(result.classification);

        const similarity =
            getSimilarityValue(result.similarity);

        card.innerHTML = `

            <div class="result-header">

                <div>

                    <h3>
                        ${escapeHTML(result.document1)}
                        vs
                        ${escapeHTML(result.document2)}
                    </h3>

                    <p>
                        Comparison ${index + 1}
                    </p>

                </div>

                <span
                    class="classification-badge"
                    style="background:${classificationColor}"
                >
                    ${escapeHTML(result.classification)}
                </span>

            </div>


            <div class="similarity-display">

                <div class="similarity-number">
                    ${similarity.toFixed(2)}%
                </div>

                <div class="similarity-label">
                    Text Similarity
                </div>

                <div class="progress-bar">

                    <div
                        class="progress-fill"
                        style="
                            width:${similarity}%;
                            background:${classificationColor};
                        "
                    ></div>

                </div>

            </div>


            <div class="result-statistics">

                <div class="result-stat">
                    <span>Document 1 Words</span>
                    <strong>${result.totalWords1}</strong>
                </div>

                <div class="result-stat">
                    <span>Document 2 Words</span>
                    <strong>${result.totalWords2}</strong>
                </div>

                <div class="result-stat">
                    <span>Unique Words D1</span>
                    <strong>${result.uniqueWords1}</strong>
                </div>

                <div class="result-stat">
                    <span>Unique Words D2</span>
                    <strong>${result.uniqueWords2}</strong>
                </div>

            </div>


            <div class="result-details">

                <h4>
                    Common Words (${result.commonWords.length})
                </h4>

                <div class="tag-container">

                    ${
            result.commonWords.length
                ?
                result.commonWords.map(word =>
                    `<span class="tag">${escapeHTML(word)}</span>`
                ).join("")
                :
                "<p>No common words found.</p>"
        }

                </div>


                <h4>
                    Common Three-Word Phrases
                    (${result.commonPhrases.length})
                </h4>

                <div class="tag-container">

                    ${
            result.commonPhrases.length
                ?
                result.commonPhrases.map(phrase =>
                    `<span class="tag phrase-tag">${escapeHTML(phrase)}</span>`
                ).join("")
                :
                "<p>No common phrases found.</p>"
        }

                </div>


                <h4>Identical Documents</h4>

                <p>
                    ${result.identical ? "YES" : "NO"}
                </p>


                <h4>Word Frequency</h4>

                <div class="frequency-grid">

                    <div>

                        <strong>
                            ${escapeHTML(result.document1)}
                        </strong>

                        <pre>${escapeHTML(
            JSON.stringify(result.frequency1, null, 2)
        )}</pre>

                    </div>


                    <div>

                        <strong>
                            ${escapeHTML(result.document2)}
                        </strong>

                        <pre>${escapeHTML(
            JSON.stringify(result.frequency2, null, 2)
        )}</pre>

                    </div>

                </div>

            </div>
        `;

        resultsContainer.appendChild(card);
    });
}


// ========================================
// REPORT HISTORY API
// ========================================

async function loadReports() {

    reportsContainer.innerHTML = `
        <div class="empty-state">
            Loading comparison history...
        </div>
    `;

    try {

        const response = await fetch(REPORTS_URL);

        if (!response.ok) {

            throw new Error(
                `Failed to retrieve reports: ${response.status}`
            );
        }

        const data = await response.json();

        if (!data.success) {

            throw new Error(
                "Could not retrieve report history."
            );
        }

        const rawReports = Array.isArray(data.reports)
            ? data.reports
            : [];

        reportHistory = getUniqueReports(rawReports);

        displayReports();

        updateStatistics();

        updateHistoryStatistics();

    } catch (error) {

        console.error("Report history error:", error);

        reportsContainer.innerHTML = `
            <div class="empty-state">
                Could not load comparison history.
                <br><br>
                ${escapeHTML(error.message)}
            </div>
        `;
    }
}


// ========================================
// REMOVE DUPLICATE PAIRS FROM DISPLAY
// ========================================

function getUniqueReports(reports) {

    const uniqueReports = new Map();

    reports.forEach(report => {

        const names = [
            report.document1,
            report.document2
        ].sort();

        const pairKey =
            `${names[0]}|||${names[1]}`;

        const existing =
            uniqueReports.get(pairKey);

        if (
            !existing ||
            Number(report.comparisonId) >
            Number(existing.comparisonId)
        ) {

            uniqueReports.set(pairKey, report);
        }
    });

    return Array.from(uniqueReports.values())
        .sort((a, b) =>
            Number(b.comparisonId) -
            Number(a.comparisonId)
        );
}


// ========================================
// HISTORY STATISTICS
// ========================================

function updateHistoryStatistics() {

    if (!historyTotal) return;

    const total = reportHistory.length;

    historyTotal.textContent = total;

    if (total === 0) {

        historyHighest.textContent = "0%";

        historyAverage.textContent = "0%";

        latestComparison.textContent =
            "Your previous comparisons";

        return;
    }

    const scores = reportHistory.map(report =>
        getSimilarityValue(report.similarity)
    );

    const highest = Math.max(...scores);

    const average =
        scores.reduce((sum, score) => sum + score, 0) /
        scores.length;

    historyHighest.textContent =
        `${highest.toFixed(2)}%`;

    historyAverage.textContent =
        `${average.toFixed(2)}%`;

    latestComparison.textContent =
        `Latest comparison: ${formatReportDate(
            reportHistory[0].comparisonDate
        )}`;
}


// ========================================
// DISPLAY REPORT HISTORY
// ========================================

function displayReports() {

    reportsContainer.innerHTML = "";

    if (reportHistory.length === 0) {

        reportsContainer.innerHTML = `
            <div class="empty-state">
                No previous comparison reports found.
            </div>
        `;

        return;
    }

    const filteredReports =
        currentFilter === "all"
            ?
            reportHistory
            :
            reportHistory.filter(report =>
                report.classification.toLowerCase() ===
                currentFilter
            );

    if (filteredReports.length === 0) {

        reportsContainer.innerHTML = `
            <div class="empty-state">
                No reports found for this category.
            </div>
        `;

        return;
    }

    filteredReports.forEach(report => {

        const card = document.createElement("div");

        card.className = "report-card";

        const classificationColor =
            getClassificationColor(report.classification);

        const similarity =
            getSimilarityValue(report.similarity);

        const formattedDate =
            formatReportDate(report.comparisonDate);

        card.innerHTML = `

            <div class="report-header">

                <div class="report-info">

                    <h3>
                        ${escapeHTML(report.document1)}
                        <span class="vs-text">vs</span>
                        ${escapeHTML(report.document2)}
                    </h3>

                    <p class="report-meta">
                        Report #${report.comparisonId}
                    </p>

                    <p class="report-date">
                        ${escapeHTML(formattedDate)}
                    </p>

                </div>

            </div>


            <div class="report-score">

                <span class="similarity-number">
                    ${similarity.toFixed(2)}%
                </span>

            </div>


            <div
                class="report-progress-bar"
                role="progressbar"
                aria-label="Similarity score"
                aria-valuenow="${similarity}"
                aria-valuemin="0"
                aria-valuemax="100"
            >

                <div
                    class="report-progress-fill"
                    style="
                        width:${similarity}%;
                        background:${classificationColor};
                    "
                ></div>

            </div>


            <div class="report-details">

                <span
                    class="classification-badge"
                    style="background:${classificationColor}"
                >
                    ${escapeHTML(report.classification)}
                </span>

                <p>
                    ${report.identical
            ? "Identical"
            : "Not identical"}
                </p>

            </div>

        `;

        reportsContainer.appendChild(card);
    });
}


// ========================================
// HISTORY FILTERS
// ========================================

filterButtons.forEach(button => {

    button.addEventListener("click", () => {

        filterButtons.forEach(filter =>
            filter.classList.remove("active")
        );

        button.classList.add("active");

        currentFilter =
            button.dataset.filter.toLowerCase();

        displayReports();
    });
});


// ========================================
// FORMAT REPORT DATE
// ========================================

function formatReportDate(dateString) {

    if (!dateString) {
        return "Date unavailable";
    }

    const date = new Date(
        dateString.replace(" ", "T")
    );

    if (isNaN(date.getTime())) {
        return dateString;
    }

    return date.toLocaleString();
}


// ========================================
// REFRESH REPORT HISTORY
// ========================================

refreshReportsButton.addEventListener("click", async () => {

    refreshReportsButton.disabled = true;

    refreshReportsButton.textContent =
        "Refreshing...";

    try {

        await loadReports();

    } finally {

        refreshReportsButton.disabled = false;

        refreshReportsButton.textContent =
            "↻ Refresh history";
    }
});


// ========================================
// NAVIGATION
// ========================================

const navItems = [
    homeNav,
    dashboardNav,
    reportsNav
].filter(Boolean);

navItems.forEach(navItem => {

    navItem.addEventListener("click", () => {

        navItems.forEach(item =>
            item.classList.remove("active")
        );

        navItem.classList.add("active");
    });
});


// ========================================
// GENERATE TEXT REPORT
// ========================================

function generateReportText(result) {

    let report = "";

    report += "========================================\n";
    report += "      PLAGIARISM DETECTION REPORT\n";
    report += "========================================\n\n";

    report += `Comparing: ${result.document1} and ${result.document2}\n\n`;

    report += "DOCUMENT STATISTICS\n";
    report += "----------------------------------------\n";

    report += `${result.document1}\n`;
    report += `Total Words: ${result.totalWords1}\n`;
    report += `Unique Words: ${result.uniqueWords1}\n\n`;

    report += `${result.document2}\n`;
    report += `Total Words: ${result.totalWords2}\n`;
    report += `Unique Words: ${result.uniqueWords2}\n\n`;

    report += "WORD FREQUENCY ANALYSIS\n";
    report += "----------------------------------------\n";

    report += `${result.document1}:\n`;

    report += JSON.stringify(
        result.frequency1,
        null,
        2
    );

    report += "\n\n";

    report += `${result.document2}:\n`;

    report += JSON.stringify(
        result.frequency2,
        null,
        2
    );

    report += "\n\n";

    report += "COMMON WORDS\n";
    report += "----------------------------------------\n";

    report += result.commonWords.join(", ") || "None";

    report += "\n\n";

    report += "COMMON THREE-WORD PHRASES\n";
    report += "----------------------------------------\n";

    report += result.commonPhrases.join("\n") || "None";

    report += "\n\n";

    report += "SIMILARITY ANALYSIS\n";
    report += "----------------------------------------\n";

    report += `Similarity Score: ${result.similarity.toFixed(2)}%\n`;

    report += `Classification: ${result.classification}\n`;

    report += `Identical Documents: ${result.identical ? "YES" : "NO"}\n\n`;

    report += "NOTE: Similarity score is based on word frequency. ";
    report += "It does not prove plagiarism.\n";

    report += "========================================\n";

    return report;
}


// ========================================
// DOWNLOAD REPORTS
// ========================================

downloadReportButton.addEventListener("click", () => {

    if (comparisonResults.length === 0) {

        alert("No reports available.");

        return;
    }

    let completeReport = "";

    comparisonResults.forEach((result, index) => {

        completeReport += `REPORT ${index + 1}\n\n`;

        completeReport += generateReportText(result);

        completeReport += "\n\n";
    });

    const blob = new Blob(
        [completeReport],
        { type: "text/plain" }
    );

    const url = URL.createObjectURL(blob);

    const link = document.createElement("a");

    link.href = url;

    link.download =
        "Plagiarism_Analysis_Report.txt";

    document.body.appendChild(link);

    link.click();

    document.body.removeChild(link);

    setTimeout(() => URL.revokeObjectURL(url), 1000);
});


// ========================================
// INITIALIZE APPLICATION
// ========================================

loadReports();

updateDocumentList();

updateStatistics();