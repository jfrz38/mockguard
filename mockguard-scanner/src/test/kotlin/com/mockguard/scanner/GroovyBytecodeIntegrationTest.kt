package com.mockguard.scanner

import com.mockguard.scanner.scanner.ClassFileScanner
import groovy.lang.GroovyClassLoader
import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GroovyBytecodeIntegrationTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `verified Groovy mock passes`() {
        compileGroovy(
            """
            import groovy.transform.CompileStatic
            import org.junit.jupiter.api.Test
            import org.mockito.Mock
            import org.mockito.Mockito

            @CompileStatic
            class GroovyVerifiedMockTest {
                @Mock
                List<String> service

                @Test
                void test() {
                    service.size()
                    Mockito.verify(service).size()
                }
            }
            """.trimIndent(),
        )

        val result = ClassFileScanner(tempDir).scan()

        assertEquals(1, result.totalClasses)
        assertTrue(result.violations.isEmpty(), "Expected no violations but got: ${result.violations}")
    }

    @Test
    fun `unverified Groovy mock fails`() {
        compileGroovy(
            """
            import groovy.transform.CompileStatic
            import org.junit.jupiter.api.Test
            import org.mockito.Mock

            @CompileStatic
            class GroovyUnverifiedMockTest {
                @Mock
                List<String> service

                @Test
                void test() {
                    service.size()
                }
            }
            """.trimIndent(),
        )

        val result = ClassFileScanner(tempDir).scan()

        assertEquals(1, result.totalClasses)
        assertEquals(1, result.violations.size)
        assertEquals("GroovyUnverifiedMockTest", result.violations[0].className)
        assertEquals("service", result.violations[0].fieldName)
        assertTrue(result.violations[0].hadInvocations)
    }

    private fun compileGroovy(source: String) {
        val configuration = CompilerConfiguration().apply {
            targetDirectory = tempDir.toFile()
        }
        GroovyClassLoader(javaClass.classLoader, configuration).use { loader ->
            loader.parseClass(source)
        }
    }
}
