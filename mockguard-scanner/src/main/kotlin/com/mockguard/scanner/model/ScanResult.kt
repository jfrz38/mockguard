package com.mockguard.scanner.model

data class ScanResult(
    val totalClasses: Int,
    val violations: List<Violation>,
    val skippedClasses: List<SkippedClass> = emptyList(),
    val baselineSummary: BaselineSummary? = null,
) {
    val scannedClasses: Int
        get() = totalClasses - skippedClasses.size
}

data class SkippedClass(
    val path: String,
    val reason: String,
)

data class BaselineSummary(
    val baselineEntries: Int,
    val knownViolations: Int,
    val newViolations: Int,
    val resolvedViolations: Int,
)
