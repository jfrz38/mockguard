package com.mockguard.scanner.output

import com.mockguard.scanner.model.ScanResult

object JsonReporter {
    fun report(result: ScanResult): String = buildString {
        appendLine("{")
        appendLine("  \"totalClasses\": ${result.totalClasses},")
        appendLine("  \"scannedClasses\": ${result.scannedClasses},")
        appendLine("  \"skippedClassCount\": ${result.skippedClasses.size},")
        appendLine("  \"violationCount\": ${result.violations.size},")
        appendLine("  \"baseline\": ${baselineJson(result)},")
        appendLine("  \"violations\": [")

        for ((index, violation) in result.violations.withIndex()) {
            appendLine("    {")
            appendLine("      \"className\": ${jsonEscape(violation.className)},")
            appendLine("      \"methodName\": ${violation.methodName?.let(::jsonEscape) ?: "null"},")
            appendLine("      \"methodDescriptor\": ${violation.methodDescriptor?.let(::jsonEscape) ?: "null"},")
            appendLine("      \"sourceFile\": ${violation.sourceFile?.let(::jsonEscape) ?: "null"},")
            appendLine("      \"lineNumber\": ${violation.lineNumber},")
            appendLine("      \"fieldName\": ${jsonEscape(violation.fieldName)},")
            appendLine("      \"fieldType\": ${jsonEscape(violation.fieldType)},")
            appendLine("      \"hadInvocations\": ${violation.hadInvocations}")
            append("    }")
            if (index < result.violations.lastIndex) appendLine(",") else appendLine()
        }

        appendLine("  ],")
        appendLine("  \"skippedClasses\": [")

        for ((index, skipped) in result.skippedClasses.withIndex()) {
            appendLine("    {")
            appendLine("      \"path\": ${jsonEscape(skipped.path)},")
            appendLine("      \"reason\": ${jsonEscape(skipped.reason)}")
            append("    }")
            if (index < result.skippedClasses.lastIndex) appendLine(",") else appendLine()
        }

        appendLine("  ]")
        append("}")
    }

    private fun baselineJson(result: ScanResult): String {
        val baseline = result.baselineSummary ?: return "null"
        return buildString {
            appendLine("{")
            appendLine("    \"baselineEntries\": ${baseline.baselineEntries},")
            appendLine("    \"knownViolations\": ${baseline.knownViolations},")
            appendLine("    \"newViolations\": ${baseline.newViolations},")
            append("    \"resolvedViolations\": ${baseline.resolvedViolations}")
            append("\n  }")
        }
    }

    private fun jsonEscape(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }
}
