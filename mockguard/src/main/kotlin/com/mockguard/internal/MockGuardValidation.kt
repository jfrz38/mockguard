package com.mockguard.internal

internal data class MockValidationState(
    val label: String,
    val mockType: String,
    val invocationCount: Int,
    val ignored: Boolean,
    val verified: Boolean,
)

internal object MockGuardValidation {
    fun findViolations(states: Collection<MockValidationState>): List<String> =
        states
            .filterNot { it.ignored || it.verified }
            .map(::buildViolation)

    fun buildSummary(violations: Collection<String>): String = buildString {
        appendLine("[MockGuard] Found ${violations.size} unverified mock(s).")
        violations.forEach { appendLine("- $it") }
        append("Verify each mock explicitly or opt out with @MockGuardIgnore / MockGuards.ignore(mock).")
    }

    fun buildViolation(state: MockValidationState): String =
        if (state.invocationCount == 0) {
            "${state.label} (${state.mockType}) was never verified. Use verifyNoInteractions(${state.label}) if the mock is intentionally unused."
        } else {
            "${state.label} (${state.mockType}) had ${state.invocationCount} invocation(s) but was never verified."
        }
}
