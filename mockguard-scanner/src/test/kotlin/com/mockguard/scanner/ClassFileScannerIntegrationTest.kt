package com.mockguard.scanner

import com.mockguard.scanner.config.TestSelector
import com.mockguard.scanner.scanner.ClassFileScanner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import javax.tools.ToolProvider
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

    @Test
    fun `keeps verification state isolated per selected method`() {
        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        val sourceFile = sourceDir.resolve("MethodSelectionTest.java")

        Files.writeString(sourceFile, methodSelectionSource())
        compileJava(sourceFile, classDir)

        val result = ClassFileScanner(
            classDir = classDir,
            tests = listOf(
                TestSelector.parse("MethodSelectionTest#unverified"),
                TestSelector.parse("MethodSelectionTest#verified"),
            ),
        ).scan()

        assertEquals(1, result.totalClasses)
        assertEquals(1, result.violations.size)
        assertEquals("unverified", result.violations.single().methodName)
        assertEquals("()V", result.violations.single().methodDescriptor)
        assertTrue(result.violations.single().hadInvocations)
    }

    @Test
    fun `requires descriptors for overloaded selected methods`() {
        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        val sourceFile = sourceDir.resolve("OverloadedSelectionTest.java")

        Files.writeString(
            sourceFile,
            unverifiedMockSource(
                className = "OverloadedSelectionTest",
                fieldName = "service",
                methods = "public void test() { service.size(); } public void test(int value) { service.size(); }",
            ),
        )
        compileJava(sourceFile, classDir)

        val error = assertFailsWith<IllegalArgumentException> {
            ClassFileScanner(
                classDir = classDir,
                tests = listOf(TestSelector.parse("OverloadedSelectionTest#test")),
            ).scan()
        }
        assertTrue(error.message.orEmpty().contains("overloaded"))

        val result = ClassFileScanner(
            classDir = classDir,
            tests = listOf(TestSelector.parse("OverloadedSelectionTest#test(I)V")),
        ).scan()
        assertEquals("(I)V", result.violations.single().methodDescriptor)
    }

    @Test
    fun `reports missing and excluded selected classes`() {
        val classDir = Files.createDirectory(tempDir.resolve("classes"))

        val missing = assertFailsWith<IllegalArgumentException> {
            ClassFileScanner(
                classDir = classDir,
                tests = listOf(TestSelector.parse("MissingTest#test")),
            ).scan()
        }
        assertTrue(missing.message.orEmpty().contains("class not found"))

        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val sourceFile = sourceDir.resolve("ExcludedSelectionTest.java")
        Files.writeString(sourceFile, unverifiedMockSource("ExcludedSelectionTest", "service"))
        compileJava(sourceFile, classDir)

        val missingMethod = assertFailsWith<IllegalArgumentException> {
            ClassFileScanner(
                classDir = classDir,
                tests = listOf(TestSelector.parse("ExcludedSelectionTest#missing")),
            ).scan()
        }
        assertTrue(missingMethod.message.orEmpty().contains("method not found"))

        val excluded = assertFailsWith<IllegalArgumentException> {
            ClassFileScanner(
                classDir = classDir,
                excludes = listOf("ExcludedSelectionTest"),
                tests = listOf(TestSelector.parse("ExcludedSelectionTest#test")),
            ).scan()
        }
        assertTrue(excluded.message.orEmpty().contains("excluded"))
    }

    @Test
    fun `selects a method from a nested class by binary name`() {
        val sourceDir = Files.createDirectory(tempDir.resolve("src"))
        val classDir = Files.createDirectory(tempDir.resolve("classes"))
        val sourceFile = sourceDir.resolve("OuterSelectionTest.java")

        Files.writeString(
            sourceFile,
            """
            import org.mockito.Mock;
            import java.util.List;

            public class OuterSelectionTest {
                public static class Nested {
                    @Mock
                    List<String> service;

                    public void test() {
                        service.size();
                    }
                }
            }
            """.trimIndent(),
        )
        compileJava(sourceFile, classDir)

        val result = ClassFileScanner(
            classDir = classDir,
            tests = listOf(TestSelector.parse("OuterSelectionTest\$Nested#test")),
        ).scan()

        assertEquals(1, result.totalClasses)
        assertEquals("OuterSelectionTest\$Nested", result.violations.single().className)
        assertEquals("test", result.violations.single().methodName)
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

    private fun methodSelectionSource(): String =
        """
        import org.mockito.Mock;
        import org.mockito.Mockito;
        import java.util.List;

        public class MethodSelectionTest {
            @Mock
            List<String> service;

            public void unverified() {
                service.size();
            }

            public void verified() {
                service.size();
                Mockito.verify(service).size();
            }
        }
        """.trimIndent()

    private fun unverifiedMockSource(
        className: String,
        fieldName: String,
        methods: String = "public void test() { $fieldName.size(); }",
    ): String =
        """
        import org.mockito.Mock;
        import java.util.List;

        public class $className {
            @Mock
            List<String> $fieldName;

            $methods
        }
        """.trimIndent()
}
