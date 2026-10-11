package com.mockguard.scanner.output

import com.mockguard.scanner.json.JsonCodec
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation

internal object SonarQubeReporter {
    fun report(result: ScanResult): String = JsonCodec.gson.toJson(
        SonarReport(result.violations.map(::issue)),
    )

    private fun issue(violation: Violation): SonarIssue {
        val filePath = violation.sourceFile?.let { sourceName ->
            if (sourceName.contains('/') || sourceName.contains('\\')) sourceName
            else "${violation.className.replace('.', '/').substringBeforeLast('/')}/$sourceName"
        } ?: "${violation.className.replace('.', '/')}.java"

        val methodContext = violation.methodName
            ?.let { " in test '$it${violation.methodDescriptor.orEmpty()}'" }
            .orEmpty()
        val message = if (violation.hadInvocations) {
            "Mock '${violation.fieldName}' (${violation.fieldType})$methodContext had invocation(s) but was never verified."
        } else {
            "Mock '${violation.fieldName}' (${violation.fieldType})$methodContext was never verified. Use verifyNoInteractions() if the mock is intentionally unused."
        }
        val line = violation.lineNumber.takeIf { it > 0 } ?: 1

        return SonarIssue(
            engineId = "mockguard-scanner",
            ruleId = "mockguard:UnverifiedMock",
            severity = "MAJOR",
            type = "CODE_SMELL",
            primaryLocation = PrimaryLocation(
                message = message,
                filePath = filePath,
                textRange = TextRange(startLine = line, endLine = line),
            ),
        )
    }
}

private data class SonarReport(val issues: List<SonarIssue>)

private data class SonarIssue(
    val engineId: String,
    val ruleId: String,
    val severity: String,
    val type: String,
    val primaryLocation: PrimaryLocation,
)

private data class PrimaryLocation(
    val message: String,
    val filePath: String,
    val textRange: TextRange,
)

private data class TextRange(val startLine: Int, val endLine: Int)
