package com.mockguard.integration

import com.mockguard.integration.fixtures.GuardedMockFailureCase
import com.mockguard.integration.fixtures.GuardedMockVerificationCase
import com.mockguard.integration.fixtures.NoGuardedMocksFallbackCase
import com.mockguard.integration.fixtures.TwoGuardedMocksFailureCase
import com.mockguard.integration.fixtures.TwoGuardedMocksVerificationCase
import com.mockguard.integration.fixtures.AllMocksGuardedCase
import com.mockguard.integration.support.MockGuardIntegrationTestSupport.runTestClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MockGuardScopedIntegrationTest {

    @Test
    fun whenNoGuardedMockExistsTheClassFallsBackToTrackingAllMocks() {
        val summary = runTestClass(NoGuardedMocksFallbackCase::class.java)

        assertEquals(1, summary.testsFailedCount)
        val failure = summary.failures.single().exception
        assertTrue(failure.message!!.contains("logger"))
        assertTrue(!failure.message!!.contains("paymentGateway"))
    }

    @Test
    fun guardedMockAllowsFocusingOnOneCriticalDependency() {
        val summary = runTestClass(GuardedMockVerificationCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun guardedMockStillFailsWhenCriticalDependencyIsNotVerified() {
        val summary = runTestClass(GuardedMockFailureCase::class.java)

        assertEquals(1, summary.testsFailedCount)
        val failure = summary.failures.single().exception
        assertTrue(failure.message!!.contains("paymentGateway"))
        assertTrue(!failure.message!!.contains("logger"))
    }

    @Test
    fun twoGuardedMocksCanBeVerifiedTogether() {
        val summary = runTestClass(TwoGuardedMocksVerificationCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun twoGuardedMocksFailIfOneCriticalDependencyIsNotVerified() {
        val summary = runTestClass(TwoGuardedMocksFailureCase::class.java)

        assertEquals(1, summary.testsFailedCount)
        val failure = summary.failures.single().exception
        assertTrue(failure.message!!.contains("auditLogger"))
        assertTrue(!failure.message!!.contains("secondaryLogger"))
    }

    @Test
    fun allMocksCanBeExplicitlyGuarded() {
        val summary = runTestClass(AllMocksGuardedCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }
}
