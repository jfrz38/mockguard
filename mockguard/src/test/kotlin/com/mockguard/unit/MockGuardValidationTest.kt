package com.mockguard.unit

import com.mockguard.internal.MockGuardValidation
import com.mockguard.internal.MockValidationState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MockGuardValidationTest {

    @Test
    fun ignoresVerifiedAndIgnoredMocksWhenBuildingViolations() {
        val violations = MockGuardValidation.findViolations(
            listOf(
                MockValidationState(
                    label = "verifiedMock",
                    mockType = "Dependency",
                    invocationCount = 1,
                    ignored = false,
                    verified = true,
                ),
                MockValidationState(
                    label = "ignoredMock",
                    mockType = "Logger",
                    invocationCount = 1,
                    ignored = true,
                    verified = false,
                ),
                MockValidationState(
                    label = "invalidMock",
                    mockType = "Gateway",
                    invocationCount = 2,
                    ignored = false,
                    verified = false,
                ),
            ),
        )

        assertEquals(
            listOf("invalidMock (Gateway) had 2 invocation(s) but was never verified."),
            violations,
        )
    }

    @Test
    fun buildsZeroInteractionViolationMessage() {
        val violation = MockGuardValidation.buildViolation(
            MockValidationState(
                label = "logger",
                mockType = "Logger",
                invocationCount = 0,
                ignored = false,
                verified = false,
            ),
        )

        assertEquals(
            "logger (Logger) was never verified. Use verifyNoInteractions(logger) if the mock is intentionally unused.",
            violation,
        )
    }

    @Test
    fun buildsSummaryWithAllViolations() {
        val message = MockGuardValidation.buildSummary(
            listOf(
                "dependency (Dependency) had 1 invocation(s) but was never verified.",
                "logger (Logger) was never verified. Use verifyNoInteractions(logger) if the mock is intentionally unused.",
            ),
        )

        assertTrue(message.contains("[MockGuard] Found 2 unverified mock(s)."))
        assertTrue(message.contains("- dependency (Dependency) had 1 invocation(s) but was never verified."))
        assertTrue(message.contains("- logger (Logger) was never verified. Use verifyNoInteractions(logger) if the mock is intentionally unused."))
        assertTrue(message.contains("Verify each mock explicitly or opt out"))
    }
}
