package com.mockguard.integration

import com.mockguard.integration.fixtures.FailModeUnverifiedMockCase
import com.mockguard.integration.fixtures.WarnModeUnverifiedMockCase
import com.mockguard.integration.support.MockGuardIntegrationTestSupport.captureStandardError
import com.mockguard.integration.support.MockGuardIntegrationTestSupport.runTestClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class MockGuardStrictModeIntegrationTest {

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
}
