package com.mockguard

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.platform.launcher.listeners.TestExecutionSummary
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MockGuardExtensionIntegrationTest {

    @Test
    fun failModeFailsWhenUsedMockIsNotVerified() {
        val summary = runTestClass(FailModeUnverifiedMockCase::class.java)

        assertEquals(1, summary.testsFailedCount)
        val failure = summary.failures.single().exception
        assertTrue(failure is AssertionError)
        assertTrue(failure.message!!.contains("dependency"))
    }

    @Test
    fun warnModePrintsWarningWithoutFailingTheTest() {
        val stderr = ByteArrayOutputStream()
        val summary = captureStandardError(stderr) {
            runTestClass(WarnModeUnverifiedMockCase::class.java)
        }

        assertEquals(1, summary.testsSucceededCount)
        assertTrue(stderr.toString().contains("[MockGuard] Found 1 unverified mock(s)."))
        assertTrue(stderr.toString().contains("dependency"))
    }

    @Test
    fun ignoredMocksAreExcludedFromValidation() {
        val summary = runTestClass(IgnoredMockCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun verifyNoInteractionsCountsAsVerification() {
        val summary = runTestClass(ZeroInteractionVerificationCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun verifyNoMoreInteractionsCountsAsVerification() {
        val summary = runTestClass(NoMoreInteractionsVerificationCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun programmaticIgnoreAllowsExplicitOptOut() {
        val summary = runTestClass(ProgrammaticIgnoreCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun verifiedMocksPassValidation() {
        val summary = runTestClass(VerifiedMockCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
        assertFalse(summary.failures.isNotEmpty())
    }

    private fun runTestClass(testClass: Class<*>) =
        SummaryGeneratingListener().also { listener ->
            val request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build()

            LauncherFactory.create().apply {
                registerTestExecutionListeners(listener)
                execute(request)
            }
        }.summary

    private fun captureStandardError(
        stream: ByteArrayOutputStream,
        block: () -> TestExecutionSummary,
    ): TestExecutionSummary {
        val original = System.err
        val capturing = PrintStream(stream, true)

        return try {
            System.setErr(capturing)
            block()
        } finally {
            capturing.flush()
            System.setErr(original)
        }
    }
}

@MockGuard(mode = StrictMode.FAIL)
class FailModeUnverifiedMockCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun fails() {
        dependency.call()
    }
}

@MockGuard(mode = StrictMode.WARN)
class WarnModeUnverifiedMockCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun warns() {
        dependency.call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class IgnoredMockCase {
    @Mock
    @MockGuardIgnore
    lateinit var logger: Logger

    @Test
    fun passes() {
        logger.info("ignored")
    }
}

@MockGuard(mode = StrictMode.FAIL)
class ZeroInteractionVerificationCase {
    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        verifyNoInteractions(logger)
    }
}

@MockGuard(mode = StrictMode.FAIL)
class ProgrammaticIgnoreCase {
    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        MockGuards.ignore(logger)
        logger.info("ignored programmatically")
    }
}

@MockGuard(mode = StrictMode.FAIL)
class VerifiedMockCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun passes() {
        dependency.call()
        verify(dependency).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class NoMoreInteractionsVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun passes() {
        dependency.call()
        verify(dependency).call()
        verifyNoMoreInteractions(dependency)
    }
}

interface Dependency {
    fun call()
}

interface Logger {
    fun info(message: String)
}
