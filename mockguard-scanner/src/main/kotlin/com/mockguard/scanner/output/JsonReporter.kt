package com.mockguard.scanner.output

import com.mockguard.scanner.json.JsonCodec
import com.mockguard.scanner.model.BaselineSummary
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.SkippedClass
import com.mockguard.scanner.model.Violation

internal object JsonReporter {
    fun report(result: ScanResult): String = JsonCodec.gson.toJson(
        JsonReport(
            totalClasses = result.totalClasses,
            scannedClasses = result.scannedClasses,
            skippedClassCount = result.skippedClasses.size,
            violationCount = result.violations.size,
            baseline = result.baselineSummary,
            violations = result.violations,
            skippedClasses = result.skippedClasses,
        ),
    )
}

private data class JsonReport(
    val totalClasses: Int,
    val scannedClasses: Int,
    val skippedClassCount: Int,
    val violationCount: Int,
    val baseline: BaselineSummary?,
    val violations: List<Violation>,
    val skippedClasses: List<SkippedClass>,
)
