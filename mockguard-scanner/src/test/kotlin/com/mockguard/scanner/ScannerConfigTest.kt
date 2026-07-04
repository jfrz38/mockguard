package com.mockguard.scanner

import com.mockguard.scanner.config.ScannerConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ScannerConfigTest {

    @Test
    fun `stores multiple class directories`() {
        val config = ScannerConfig(classDirs = listOf("build/classes/java/test", "build/classes/kotlin/test"))

        assertEquals(listOf("build/classes/java/test", "build/classes/kotlin/test"), config.classDirs)
    }

    @Test
    fun `stores include and exclude patterns`() {
        val config = ScannerConfig(
            classDirs = listOf("build/classes/kotlin/test"),
            includes = listOf("*ServiceTest"),
            excludes = listOf("*Generated*"),
        )

        assertEquals(listOf("*ServiceTest"), config.includes)
        assertEquals(listOf("*Generated*"), config.excludes)
    }

    @Test
    fun `parses valid mode strings`() {
        assertEquals(ScannerConfig.OutputMode.FAIL, ScannerConfig.OutputMode.fromString("FAIL"))
        assertEquals(ScannerConfig.OutputMode.WARN, ScannerConfig.OutputMode.fromString("WARN"))
        assertEquals(ScannerConfig.OutputMode.OFF, ScannerConfig.OutputMode.fromString("OFF"))
        assertEquals(ScannerConfig.OutputMode.FAIL, ScannerConfig.OutputMode.fromString("fail"))
        assertEquals(ScannerConfig.OutputMode.WARN, ScannerConfig.OutputMode.fromString("warn"))
    }

    @Test
    fun `throws on invalid mode`() {
        assertThrows<IllegalArgumentException> {
            ScannerConfig.OutputMode.fromString("INVALID")
        }
    }

    @Test
    fun `parses valid format strings`() {
        assertEquals(ScannerConfig.OutputFormat.CONSOLE, ScannerConfig.OutputFormat.fromString("CONSOLE"))
        assertEquals(ScannerConfig.OutputFormat.JSON, ScannerConfig.OutputFormat.fromString("JSON"))
        assertEquals(ScannerConfig.OutputFormat.SONARQUBE, ScannerConfig.OutputFormat.fromString("SONARQUBE"))
        assertEquals(ScannerConfig.OutputFormat.CONSOLE, ScannerConfig.OutputFormat.fromString("console"))
    }

    @Test
    fun `throws on invalid format`() {
        assertThrows<IllegalArgumentException> {
            ScannerConfig.OutputFormat.fromString("INVALID")
        }
    }

    @Test
    fun `parses valid fail-on strings`() {
        assertEquals(ScannerConfig.FailOn.VIOLATIONS, ScannerConfig.FailOn.fromString("VIOLATIONS"))
        assertEquals(ScannerConfig.FailOn.NEW, ScannerConfig.FailOn.fromString("NEW"))
        assertEquals(ScannerConfig.FailOn.NEW, ScannerConfig.FailOn.fromString("new"))
    }

    @Test
    fun `throws on invalid fail-on`() {
        assertThrows<IllegalArgumentException> {
            ScannerConfig.FailOn.fromString("INVALID")
        }
    }
}
