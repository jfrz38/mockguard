package com.mockguard.scanner.output

import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation

object SonarQubeReporter {
    fun report(result: ScanResult): String = buildString {
        appendLine("{")
        appendLine("  \"issues\": [")

        for ((index, v) in result.violations.withIndex()) {
            val filePath = v.sourceFile?.let { sourceName ->
                if (sourceName.contains('/') || sourceName.contains('\\')) sourceName
                else "${v.className.replace('.', '/').substringBeforeLast('/')}/$sourceName"
            } ?: "${v.className.replace('.', '/')}.java"

            val message = if (v.hadInvocations) {
                "Mock '${v.fieldName}' (${v.fieldType}) had invocation(s) but was never verified."
            } else {
                "Mock '${v.fieldName}' (${v.fieldType}) was never verified. Use verifyNoInteractions() if the mock is intentionally unused."
            }

            val line = if (v.lineNumber > 0) v.lineNumber else 1

            appendLine("    {")
            appendLine("      \"engineId\": \"mockguard-scanner\",")
            appendLine("      \"ruleId\": \"mockguard:UnverifiedMock\",")
            appendLine("      \"severity\": \"MAJOR\",")
            appendLine("      \"type\": \"CODE_SMELL\",")
            appendLine("      \"primaryLocation\": {")
            appendLine("        \"message\": ${jsonEscape(message)},")
            appendLine("        \"filePath\": ${jsonEscape(filePath)},")
            appendLine("        \"textRange\": {")
            appendLine("          \"startLine\": $line,")
            appendLine("          \"endLine\": $line")
            appendLine("        }")
            appendLine("      }")
            append("    }")
            if (index < result.violations.lastIndex) appendLine(",") else appendLine()
        }

        appendLine("  ]")
        append("}")
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
