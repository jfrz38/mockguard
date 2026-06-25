package com.mockguard.scanner

import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.BaselineSummary
import com.mockguard.scanner.model.SkippedClass
import com.mockguard.scanner.model.Violation
import com.mockguard.scanner.output.ConsoleReporter
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

class ConsoleReporterTest {

    @Test
    fun `reports success when no violations`() {
        val result = ScanResult(totalClasses = 10, violations = emptyList())
        val report = ConsoleReporter.report(result)
        assertContains(report, "All mocks verified")
        assertContains(report, "10")
    }

    @Test
    fun `reports violations with details`() {
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
        val result = ScanResult(totalClasses = 5, violations = violations)
        val report = ConsoleReporter.report(result)
        assertContains(report, "com.example.MyTest")
        assertContains(report, "service")
        assertContains(report, "com.example.Service")
        assertContains(report, "never verified")
        assertContains(report, "1 violation")
    }

    @Test
    fun `reports mock without invocations with different message`() {
        val violations = listOf(
            Violation(
                className = "com.example.MyTest",
                sourceFile = null,
                lineNumber = 0,
                fieldName = "logger",
                fieldType = "Logger",
                hadInvocations = false,
            ),
        )
        val result = ScanResult(totalClasses = 1, violations = violations)
        val report = ConsoleReporter.report(result)
        assertContains(report, "verifyNoInteractions")
        assertContains(report, "Logger")
    }

    @Test
    fun `reports skipped class count without verbose details`() {
        val result = ScanResult(
            totalClasses = 2,
            violations = emptyList(),
            skippedClasses = listOf(SkippedClass(path = "Broken.class", reason = "invalid bytecode")),
        )

        val report = ConsoleReporter.report(result)

        assertContains(report, "Scanned 1 test class")
        assertContains(report, "Skipped 1 class file")
        assertContains(report, "--verbose")
    }

    @Test
    fun `reports skipped class details in verbose mode`() {
        val result = ScanResult(
            totalClasses = 1,
            violations = emptyList(),
            skippedClasses = listOf(SkippedClass(path = "Broken.class", reason = "invalid bytecode")),
        )

        val report = ConsoleReporter.report(result, verbose = true)

        assertContains(report, "Broken.class")
        assertContains(report, "invalid bytecode")
    }

    @Test
    fun `reports baseline summary`() {
        val result = ScanResult(
            totalClasses = 2,
            violations = emptyList(),
            baselineSummary = BaselineSummary(
                baselineEntries = 3,
                knownViolations = 2,
                newViolations = 0,
                resolvedViolations = 1,
            ),
        )

        val report = ConsoleReporter.report(result)

        assertContains(report, "Baseline: 2 known, 0 new, 1 resolved, 3 total baseline entries")
    }
}
