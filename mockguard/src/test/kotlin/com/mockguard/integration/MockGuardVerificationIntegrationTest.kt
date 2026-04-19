package com.mockguard.integration

import com.mockguard.integration.fixtures.NoMoreInteractionsVerificationCase
import com.mockguard.integration.fixtures.NoInteractionsVerifiedInAfterEachCase
import com.mockguard.integration.fixtures.VerifiedMockCase
import com.mockguard.integration.fixtures.VerifiedInAfterEachCase
import com.mockguard.integration.fixtures.ZeroInteractionVerificationCase
import com.mockguard.integration.support.MockGuardIntegrationTestSupport.runTestClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class MockGuardVerificationIntegrationTest {

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
    fun verifiedMocksPassValidation() {
        val summary = runTestClass(VerifiedMockCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
        assertFalse(summary.failures.isNotEmpty())
    }

    @Test
    fun verifyInAfterEachCountsAsVerification() {
        val summary = runTestClass(VerifiedInAfterEachCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun verifyNoInteractionsInAfterEachCountsAsVerification() {
        val summary = runTestClass(NoInteractionsVerifiedInAfterEachCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }
}
