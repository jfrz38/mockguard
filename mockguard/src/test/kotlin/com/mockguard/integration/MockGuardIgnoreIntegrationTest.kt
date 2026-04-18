package com.mockguard.integration

import com.mockguard.integration.fixtures.IgnoredMockCase
import com.mockguard.integration.fixtures.ProgrammaticIgnoreCase
import com.mockguard.integration.support.MockGuardIntegrationTestSupport.runTestClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MockGuardIgnoreIntegrationTest {

    @Test
    fun ignoredMocksAreExcludedFromValidation() {
        val summary = runTestClass(IgnoredMockCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun programmaticIgnoreAllowsExplicitOptOut() {
        val summary = runTestClass(ProgrammaticIgnoreCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }
}
