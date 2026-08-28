package com.mockguard.scanner

import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.BaselineSummary
import com.mockguard.scanner.model.SkippedClass
import com.mockguard.scanner.model.Violation
import com.mockguard.scanner.output.JsonReporter
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

class JsonReporterTest {

    @Test
    fun `produces machine readable JSON with violations`() {
        val result = ScanResult(
            totalClasses = 2,
            violations = listOf(
                Violation(
                    className = "com.example.MyTest",
                    sourceFile = "MyTest.kt",
                    lineNumber = 12,
                    fieldName = "service",
                    fieldType = "com.example.Service",
                    hadInvocations = true,
                    methodName = "test",
                    methodDescriptor = "()V",
                ),
            ),
        )

        val report = JsonReporter.report(result)

        assertContains(report, "\"totalClasses\": 2")
        assertContains(report, "\"scannedClasses\": 2")
        assertContains(report, "\"skippedClassCount\": 0")
        assertContains(report, "\"violationCount\": 1")
        assertContains(report, "\"className\": \"com.example.MyTest\"")
        assertContains(report, "\"methodName\": \"test\"")
        assertContains(report, "\"methodDescriptor\": \"()V\"")
        assertContains(report, "\"sourceFile\": \"MyTest.kt\"")
        assertContains(report, "\"hadInvocations\": true")
    }

    @Test
    fun `escapes JSON string values`() {
        val result = ScanResult(
            totalClasses = 1,
            violations = listOf(
                Violation(
                    className = "com.example.Quoted\"Test",
                    sourceFile = null,
                    lineNumber = 0,
                    fieldName = "service",
                    fieldType = "Service\\Type",
                    hadInvocations = false,
                ),
            ),
        )

        val report = JsonReporter.report(result)

        assertContains(report, "com.example.Quoted\\\"Test")
        assertContains(report, "\"sourceFile\": null")
        assertContains(report, "Service\\\\Type")
    }

    @Test
    fun `includes skipped classes`() {
        val result = ScanResult(
            totalClasses = 2,
            violations = emptyList(),
            skippedClasses = listOf(SkippedClass(path = "Broken.class", reason = "invalid bytecode")),
        )

        val report = JsonReporter.report(result)

        assertContains(report, "\"scannedClasses\": 1")
        assertContains(report, "\"skippedClassCount\": 1")
        assertContains(report, "\"skippedClasses\": [")
        assertContains(report, "\"path\": \"Broken.class\"")
        assertContains(report, "\"reason\": \"invalid bytecode\"")
    }

    @Test
    fun `includes baseline summary`() {
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

        val report = JsonReporter.report(result)

        assertContains(report, "\"baselineEntries\": 3")
        assertContains(report, "\"knownViolations\": 2")
        assertContains(report, "\"newViolations\": 0")
        assertContains(report, "\"resolvedViolations\": 1")
    }
}
