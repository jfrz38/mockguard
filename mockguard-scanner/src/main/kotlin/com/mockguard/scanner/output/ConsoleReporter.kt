package com.mockguard.scanner.output

import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation

object ConsoleReporter {
    fun report(result: ScanResult, verbose: Boolean = false): String {
        if (result.violations.isEmpty()) {
            return buildString {
                appendLine("[mockguard-scanner] All mocks verified. ✓")
                appendLine("Scanned ${result.scannedClasses} test class(es).")
                appendBaselineSummary(result)
                appendSkippedClasses(result, verbose)
            }
        }

        val violationsByLocation = result.violations.groupBy { it.locationName() }
        val classCount = result.violations.map { it.className }.distinct().size

        return buildString {
            appendLine("[mockguard-scanner] Found ${result.violations.size} unverified mock(s) in $classCount class(es).")
            appendLine()

            for ((location, violations) in violationsByLocation) {
                appendLine("❌ $location")
                for (v in violations) {
                    val reason = if (v.hadInvocations) {
                        "had invocation(s) but was never verified"
                    } else {
                        "never verified. Use verifyNoInteractions() if intentionally unused"
                    }
                    appendLine("   ${v.fieldName} (${v.fieldType}) - $reason")
                }
            }

            appendLine()
            appendLine("${result.violations.size} violation(s) in ${result.scannedClasses} test class(es) scanned")
            appendBaselineSummary(result)
            appendSkippedClasses(result, verbose)
        }
    }

    private fun Violation.locationName(): String =
        methodName?.let { "$className#$it${methodDescriptor.orEmpty()}" } ?: className

    private fun StringBuilder.appendBaselineSummary(result: ScanResult) {
        val baseline = result.baselineSummary ?: return
        appendLine(
            "Baseline: ${baseline.knownViolations} known, " +
                "${baseline.newViolations} new, " +
                "${baseline.resolvedViolations} resolved, " +
                "${baseline.baselineEntries} total baseline entr${if (baseline.baselineEntries == 1) "y" else "ies"}.",
        )
    }

    private fun StringBuilder.appendSkippedClasses(result: ScanResult, verbose: Boolean) {
        if (result.skippedClasses.isEmpty()) {
            return
        }

        appendLine("Skipped ${result.skippedClasses.size} class file(s). Use --verbose for details.")

        if (!verbose) {
            return
        }

        for (skipped in result.skippedClasses) {
            appendLine("   ${skipped.path} - ${skipped.reason}")
        }
    }
}
