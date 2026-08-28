package com.mockguard.scanner

import com.mockguard.scanner.model.Violation
import com.mockguard.scanner.scanner.MockGuardClassVisitor
import net.bytebuddy.jar.asm.ClassReader
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.URI
import javax.tools.JavaCompiler
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject
import javax.tools.ToolProvider
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JavaBytecodeIntegrationTest {

    @Test
    fun `verified mock passes`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import java.util.List;

            public class VerifiedMockTest {
                @Mock
                List<String> service;

                @Test
                public void test() {
                    Mockito.verify(service);
                }
            }
            """,
        )
        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `unverified mock with invocation fails`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import java.util.List;

            public class UnverifiedMockTest {
                @Mock
                List<String> service;

                @Test
                public void test() {
                    service.size();
                }
            }
            """,
        )
        assertEquals(1, violations.size)
        assertEquals("service", violations[0].fieldName)
        assertTrue(violations[0].hadInvocations, "Expected hadInvocations=true")
    }

    @Test
    fun `verifyNoInteractions passes as valid verification`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import java.util.List;

            public class VerifyNoInteractionsTest {
                @Mock
                List<String> service;

                @Test
                public void test() {
                    Mockito.verifyNoInteractions(service);
                }
            }
            """,
        )
        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `ignored mock with MockGuardIgnore passes`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import com.mockguard.MockGuardIgnore;
            import java.util.List;

            public class IgnoredMockTest {
                @Mock
                @MockGuardIgnore
                List<String> service;

                @Test
                public void test() {
                    service.size();
                }
            }
            """,
        )
        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `guarded mock scope ignores unguarded mocks`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import com.mockguard.GuardedMock;
            import java.util.List;

            public class GuardedMockTest {
                @GuardedMock
                @Mock
                List<String> guardedService;

                @Mock
                List<String> unguardedService;

                @Test
                public void test() {
                    guardedService.size();
                    unguardedService.size();
                    Mockito.verify(guardedService).size();
                }
            }
            """,
        )
        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `mock without invocation and without verify fails`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import java.util.List;

            public class NoInvocationNoVerifyTest {
                @Mock
                List<String> service;

                @Test
                public void test() {
                }
            }
            """,
        )
        assertEquals(1, violations.size)
        assertEquals("service", violations[0].fieldName)
        assertEquals(false, violations[0].hadInvocations)
    }

    @Test
    fun `verifyNoMoreInteractions passes as valid verification`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import java.util.List;

            public class VerifyNoMoreInteractionsTest {
                @Mock
                List<String> service;

                @Test
                public void test() {
                    Mockito.verifyNoMoreInteractions(service);
                }
            }
            """,
        )
        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `partial verification detects unverified mock`() {
        val violations = compileAndScan(
            """
            import org.mockito.Mock;
            import org.mockito.Mockito;
            import org.junit.jupiter.api.Test;
            import java.util.List;

            public class PartialVerifyTest {
                @Mock
                List<String> service1;

                @Mock
                List<String> service2;

                @Test
                public void test() {
                    service1.size();
                    service2.size();
                    Mockito.verify(service1).size();
                }
            }
            """,
        )
        assertEquals(1, violations.size)
        assertEquals("service2", violations[0].fieldName)
        assertTrue(violations[0].hadInvocations, "Expected hadInvocations=true")
    }

    private fun compileAndScan(javaSource: String): List<Violation> {
        val compiler = ToolProvider.getSystemJavaCompiler()
            ?: throw IllegalStateException("No Java compiler available. Run with JDK, not JRE.")

        val className = extractClassName(javaSource)
        val sourceObject = InMemorySourceFile(className, javaSource)

        val classpath = buildClasspath()
        val options = listOf("-cp", classpath)

        val stdFileManager = compiler.getStandardFileManager(null, null, null)
        val fileManager = InMemoryFileManager(stdFileManager)

        val task = compiler.getTask(null, fileManager, null, options, null, listOf(sourceObject))
        val success = task.call()

        assertTrue(success, "Compilation failed for:\n$javaSource")

        val classBytes = fileManager.getClassBytes(className)
            ?: throw IllegalStateException("No bytecode produced for $className")

        val reader = ClassReader(classBytes)
        val visitor = MockGuardClassVisitor()
        reader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        return visitor.getViolations()
    }

    private fun extractClassName(source: String): String {
        val classRegex = Regex("""(?:public\s+)?(?:class|interface)\s+(\w+)""")
        return classRegex.find(source)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("Cannot find class name in source")
    }

    private fun buildClasspath(): String {
        val separator = System.getProperty("path.separator")
        return System.getProperty("java.class.path", "")
            .split(separator)
            .filter { it.isNotBlank() }
            .joinToString(separator)
    }
}

internal class InMemorySourceFile(
    className: String,
    private val sourceCode: String,
) : SimpleJavaFileObject(
    URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension),
    JavaFileObject.Kind.SOURCE,
) {
    override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence = sourceCode
}

internal class InMemoryFileManager(
    stdFileManager: javax.tools.StandardJavaFileManager,
) : javax.tools.ForwardingJavaFileManager<javax.tools.StandardJavaFileManager>(stdFileManager) {

    private val outputs = mutableMapOf<String, ByteArrayOutputStream>()

    override fun getJavaFileForOutput(
        location: javax.tools.JavaFileManager.Location?,
        className: String?,
        kind: JavaFileObject.Kind?,
        sibling: javax.tools.FileObject?,
    ): JavaFileObject {
        val baos = ByteArrayOutputStream()
        if (className != null) outputs[className] = baos
        return InMemoryClassFile(className ?: "Unknown", baos)
    }

    fun getClassBytes(className: String): ByteArray? {
        return outputs[className]?.toByteArray()
    }
}

internal class InMemoryClassFile(
    className: String,
    private val outputStream: OutputStream,
) : SimpleJavaFileObject(
    URI.create("bytes:///" + className.replace('.', '/') + JavaFileObject.Kind.CLASS.extension),
    JavaFileObject.Kind.CLASS,
) {
    override fun openOutputStream(): OutputStream = outputStream
}
