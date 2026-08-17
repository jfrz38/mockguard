package com.mockguard.consumer

import com.mockguard.consumer.fixtures.ScannerMethodSelectionCase
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolutePathString
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.fail

class ScannerConsumerIntegrationTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `scanner selects consumer methods independently`() {
        val result = runScanner(
            "--test=$FIXTURE_CLASS#verified",
            "--test=$FIXTURE_CLASS#unverified",
            "--format=json",
            "--mode=OFF",
        )

        assertEquals(0, result.exitCode, result.diagnostics())
        assertContains(result.stdout, "\"totalClasses\": 1")
        assertContains(result.stdout, "\"violationCount\": 1")
        assertContains(result.stdout, "\"methodName\": \"unverified\"")
        assertContains(result.stdout, "\"methodDescriptor\": \"()V\"")
        assertFalse(result.stdout.contains("\"methodName\": \"verified\""), result.diagnostics())
    }

    @Test
    fun `scanner returns failure for an unverified selected consumer method`() {
        val result = runScanner(
            "--test=$FIXTURE_CLASS#unverified",
            "--format=json",
            "--mode=FAIL",
        )

        assertEquals(1, result.exitCode, result.diagnostics())
        assertContains(result.stdout, "\"violationCount\": 1")
        assertContains(result.stdout, "\"methodName\": \"unverified\"")
    }

    private fun runScanner(vararg arguments: String): ScannerProcessResult {
        val stdoutFile = tempDir.resolve("scanner-stdout-${System.nanoTime()}.txt")
        val stderrFile = tempDir.resolve("scanner-stderr-${System.nanoTime()}.txt")
        val command = listOf(
            javaExecutable().absolutePathString(),
            "-jar",
            scannerJar().absolutePathString(),
            "--class-dir=${consumerClassDirectory().absolutePathString()}",
        ) + arguments

        val process = ProcessBuilder(command)
            .redirectOutput(stdoutFile.toFile())
            .redirectError(stderrFile.toFile())
            .start()

        if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            fail("Scanner process timed out. Command: ${command.joinToString(" ")}")
        }

        return ScannerProcessResult(
            exitCode = process.exitValue(),
            stdout = Files.readString(stdoutFile),
            stderr = Files.readString(stderrFile),
            command = command,
        )
    }

    private fun scannerJar(): Path = Path.of(
        requireNotNull(System.getProperty(SCANNER_JAR_PROPERTY)) {
            "Missing system property $SCANNER_JAR_PROPERTY"
        },
    )

    private fun consumerClassDirectory(): Path = Path.of(
        ScannerMethodSelectionCase::class.java.protectionDomain.codeSource.location.toURI(),
    )

    private fun javaExecutable(): Path {
        val executable = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
            "java.exe"
        } else {
            "java"
        }
        return Path.of(System.getProperty("java.home"), "bin", executable)
    }

    private data class ScannerProcessResult(
        val exitCode: Int,
        val stdout: String,
        val stderr: String,
        val command: List<String>,
    ) {
        fun diagnostics(): String = buildString {
            appendLine("Command: ${command.joinToString(" ")}")
            appendLine("Exit code: $exitCode")
            appendLine("stdout:")
            appendLine(stdout)
            appendLine("stderr:")
            append(stderr)
        }
    }

    private companion object {
        const val FIXTURE_CLASS = "com.mockguard.consumer.fixtures.ScannerMethodSelectionCase"
        const val PROCESS_TIMEOUT_SECONDS = 30L
        const val SCANNER_JAR_PROPERTY = "mockguard.scanner.jar"
    }
}
