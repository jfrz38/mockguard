package com.mockguard.scanner

import com.mockguard.scanner.scanner.ClassFileScanner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import javax.tools.ToolProvider
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClassFileScannerIntegrationTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `scans class files from a real directory`() {
        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        val sourceFile = sourceDir.resolve("DirectoryScannerTest.java")

        Files.writeString(
            sourceFile,
            """
            import org.junit.jupiter.api.Test;
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import java.util.List;

            public class DirectoryScannerTest {
                @Mock
                List<String> verifiedService;

                @Mock
                List<String> unverifiedService;

                @Test
                public void test() {
                    verifiedService.size();
                    unverifiedService.size();
                    Mockito.verify(verifiedService).size();
                }
            }
            """.trimIndent(),
        )

        compileJava(sourceFile, classDir)

        val result = ClassFileScanner(classDir).scan()

        assertEquals(1, result.totalClasses)
        assertEquals(1, result.scannedClasses)
        assertTrue(result.skippedClasses.isEmpty())
        assertEquals(1, result.violations.size)
        assertEquals("DirectoryScannerTest", result.violations[0].className)
        assertEquals("unverifiedService", result.violations[0].fieldName)
        assertTrue(result.violations[0].hadInvocations)
    }

    @Test
    fun `reports skipped class files`() {
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        Files.write(classDir.resolve("Broken.class"), byteArrayOf(1, 2, 3, 4))

        val result = ClassFileScanner(classDir).scan()

        assertEquals(1, result.totalClasses)
        assertEquals(0, result.scannedClasses)
        assertEquals(1, result.skippedClasses.size)
        assertEquals("Broken.class", result.skippedClasses[0].path)
    }

    @Test
    fun `filters scanned classes with include patterns`() {
        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        val included = sourceDir.resolve("IncludedScannerTest.java")
        val skipped = sourceDir.resolve("SkippedScannerTest.java")

        Files.writeString(included, unverifiedMockSource("IncludedScannerTest", "includedService"))
        Files.writeString(skipped, unverifiedMockSource("SkippedScannerTest", "skippedService"))

        compileJava(included, classDir)
        compileJava(skipped, classDir)

        val result = ClassFileScanner(classDir, includes = listOf("*IncludedScannerTest")).scan()

        assertEquals(1, result.totalClasses)
        assertEquals(1, result.violations.size)
        assertEquals("IncludedScannerTest", result.violations[0].className)
        assertEquals("includedService", result.violations[0].fieldName)
    }

    @Test
    fun `filters scanned classes with exclude patterns`() {
        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        val included = sourceDir.resolve("IncludedScannerTest.java")
        val excluded = sourceDir.resolve("ExcludedScannerTest.java")

        Files.writeString(included, unverifiedMockSource("IncludedScannerTest", "includedService"))
        Files.writeString(excluded, unverifiedMockSource("ExcludedScannerTest", "excludedService"))

        compileJava(included, classDir)
        compileJava(excluded, classDir)

        val result = ClassFileScanner(classDir, excludes = listOf("ExcludedScannerTest.class")).scan()

        assertEquals(1, result.totalClasses)
        assertEquals(1, result.violations.size)
        assertEquals("IncludedScannerTest", result.violations[0].className)
        assertEquals("includedService", result.violations[0].fieldName)
    }

    private fun compileJava(sourceFile: Path, classDir: Path) {
        val compiler = ToolProvider.getSystemJavaCompiler()
            ?: throw IllegalStateException("No Java compiler available. Run with JDK, not JRE.")
        val separator = System.getProperty("path.separator")
        val classpath = System.getProperty("java.class.path", "")
            .split(separator)
            .filter { it.isNotBlank() }
            .joinToString(separator)

        val result = compiler.run(
            null,
            null,
            null,
            "-cp",
            classpath,
            "-d",
            classDir.toString(),
            sourceFile.toString(),
        )

        assertEquals(0, result, "javac failed for $sourceFile")
    }

    private fun unverifiedMockSource(className: String, fieldName: String): String =
        """
        import org.junit.jupiter.api.Test;
        import org.mockito.Mock;
        import java.util.List;

        public class $className {
            @Mock
            List<String> $fieldName;

            @Test
            public void test() {
                $fieldName.size();
            }
        }
        """.trimIndent()
}
