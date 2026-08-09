package com.mockguard.scanner

import com.mockguard.scanner.config.TestSelector
import com.mockguard.scanner.fixtures.KotlinBacktickMethodFixture
import com.mockguard.scanner.fixtures.KotlinCustomVerifyHelperFixture
import com.mockguard.scanner.fixtures.KotlinDirectMockitoVerificationFixture
import com.mockguard.scanner.fixtures.KotlinMockitoKotlinNoInteractionsFixture
import com.mockguard.scanner.fixtures.KotlinMockitoKotlinNoMoreInteractionsFixture
import com.mockguard.scanner.fixtures.KotlinMockitoKotlinVerificationFixture
import com.mockguard.scanner.model.Violation
import com.mockguard.scanner.scanner.MockGuardClassVisitor
import net.bytebuddy.jar.asm.ClassReader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinBytecodeIntegrationTest {

    @Test
    fun `direct Mockito verification in Kotlin bytecode passes`() {
        val violations = scan(KotlinDirectMockitoVerificationFixture::class.java)

        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `mockito-kotlin verify in Kotlin bytecode passes`() {
        val violations = scan(KotlinMockitoKotlinVerificationFixture::class.java)

        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `mockito-kotlin verifyNoInteractions in Kotlin bytecode passes`() {
        val violations = scan(KotlinMockitoKotlinNoInteractionsFixture::class.java)

        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `mockito-kotlin verifyNoMoreInteractions in Kotlin bytecode passes`() {
        val violations = scan(KotlinMockitoKotlinNoMoreInteractionsFixture::class.java)

        assertTrue(violations.isEmpty(), "Expected no violations but got: $violations")
    }

    @Test
    fun `custom Kotlin helper named verify does not count as Mockito verification`() {
        val violations = scan(KotlinCustomVerifyHelperFixture::class.java)

        assertEquals(1, violations.size)
        assertEquals("dependency", violations[0].fieldName)
    }

    @Test
    fun `selects Kotlin backtick method by its JVM name`() {
        val selector = TestSelector.parse(
            "${KotlinBacktickMethodFixture::class.java.name}#unverified method",
        )
        val violations = scan(KotlinBacktickMethodFixture::class.java, listOf(selector))

        assertEquals(1, violations.size)
        assertEquals("unverified method", violations.single().methodName)
        assertEquals("()V", violations.single().methodDescriptor)
    }

    private fun scan(
        type: Class<*>,
        tests: List<TestSelector> = emptyList(),
    ): List<Violation> {
        val resourceName = type.name.replace('.', '/') + ".class"
        val classBytes = type.classLoader.getResourceAsStream(resourceName)?.use { it.readBytes() }
            ?: error("Class bytes not found for ${type.name}")

        val reader = ClassReader(classBytes)
        val visitor = MockGuardClassVisitor(tests)
        reader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        return visitor.getViolations()
    }
}
