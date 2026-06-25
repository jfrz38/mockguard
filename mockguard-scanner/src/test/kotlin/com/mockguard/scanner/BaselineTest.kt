package com.mockguard.scanner

import com.mockguard.scanner.baseline.Baseline
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BaselineTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `writes baseline with stable violation keys`() {
        val baselineFile = tempDir.resolve("mockguard-baseline.json")

        Baseline.write(
            baselineFile,
            listOf(
                violation(className = "com.example.BTest", fieldName = "service"),
                violation(className = "com.example.ATest", fieldName = "logger"),
            ),
        )

        val baseline = Files.readString(baselineFile)

        assertContains(baseline, "\"version\": 1")
        assertContains(baseline, "\"className\": \"com.example.ATest\"")
        assertContains(baseline, "\"fieldName\": \"logger\"")
        assertContains(baseline, "\"fieldType\": \"com.example.Dependency\"")
    }

    @Test
    fun `applies baseline and keeps only new violations`() {
        val baselineFile = tempDir.resolve("mockguard-baseline.json")
        Baseline.write(
            baselineFile,
            listOf(
                violation(className = "com.example.KnownTest", fieldName = "known"),
                violation(className = "com.example.ResolvedTest", fieldName = "resolved"),
            ),
        )

        val result = ScanResult(
            totalClasses = 2,
            violations = listOf(
                violation(className = "com.example.KnownTest", fieldName = "known"),
                violation(className = "com.example.NewTest", fieldName = "new"),
            ),
        )

        val filtered = Baseline.apply(result, baselineFile)

        assertEquals(1, filtered.violations.size)
        assertEquals("new", filtered.violations[0].fieldName)
        assertEquals(2, filtered.baselineSummary?.baselineEntries)
        assertEquals(1, filtered.baselineSummary?.knownViolations)
        assertEquals(1, filtered.baselineSummary?.newViolations)
        assertEquals(1, filtered.baselineSummary?.resolvedViolations)
    }

    @Test
    fun `round trips escaped strings`() {
        val baselineFile = tempDir.resolve("mockguard-baseline.json")
        Baseline.write(
            baselineFile,
            listOf(violation(className = "com.example.Quoted\"Test", fieldName = "service\\name")),
        )

        val result = ScanResult(
            totalClasses = 1,
            violations = listOf(violation(className = "com.example.Quoted\"Test", fieldName = "service\\name")),
        )

        val filtered = Baseline.apply(result, baselineFile)

        assertEquals(0, filtered.violations.size)
        assertEquals(1, filtered.baselineSummary?.knownViolations)
    }

    private fun violation(
        className: String,
        fieldName: String,
        fieldType: String = "com.example.Dependency",
    ): Violation = Violation(
        className = className,
        sourceFile = "${className.substringAfterLast('.')}.kt",
        lineNumber = 1,
        fieldName = fieldName,
        fieldType = fieldType,
        hadInvocations = true,
    )
}
