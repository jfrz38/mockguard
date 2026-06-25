package com.mockguard.scanner

import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation
import com.mockguard.scanner.output.SonarQubeReporter
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

class SonarQubeReporterTest {

    @Test
    fun `produces valid JSON with violations`() {
        val violations = listOf(
            Violation(
                className = "com.example.MyTest",
                sourceFile = "MyTest.java",
                lineNumber = 15,
                fieldName = "service",
                fieldType = "com.example.Service",
                hadInvocations = true,
            ),
        )
        val result = ScanResult(totalClasses = 1, violations = violations)
        val report = SonarQubeReporter.report(result)

        assertContains(report, "mockguard-scanner")
        assertContains(report, "mockguard:UnverifiedMock")
        assertContains(report, "MAJOR")
        assertContains(report, "CODE_SMELL")
        assertContains(report, "never verified")
        assertContains(report, "15")
    }

    @Test
    fun `produces empty issues array when no violations`() {
        val result = ScanResult(totalClasses = 5, violations = emptyList())
        val report = SonarQubeReporter.report(result)
        assertContains(report, "\"issues\": [")
        assertContains(report, "]")
    }

    @Test
    fun `handles null source file gracefully`() {
        val violations = listOf(
            Violation(
                className = "com.example.MyTest",
                sourceFile = null,
                lineNumber = 0,
                fieldName = "service",
                fieldType = "Service",
                hadInvocations = false,
            ),
        )
        val result = ScanResult(totalClasses = 1, violations = violations)
        val report = SonarQubeReporter.report(result)
        assertContains(report, ".java")
    }
}
