package com.mockguard.scanner.scanner

internal enum class VerificationCall {
    Verify,
    VerifyNoInteractions,
    VerifyNoMoreInteractions,
    InOrder,
}

internal interface VerificationCallMatcher {
    fun match(owner: String, name: String, descriptor: String): VerificationCall?
}

internal object MockitoVerificationCallMatcher : VerificationCallMatcher {
    override fun match(owner: String, name: String, descriptor: String): VerificationCall? {
        if (owner != "org/mockito/Mockito") {
            return null
        }

        return when (name) {
            "verify" -> VerificationCall.Verify
            "verifyNoInteractions" -> VerificationCall.VerifyNoInteractions
            "verifyNoMoreInteractions" -> VerificationCall.VerifyNoMoreInteractions
            "inOrder" -> VerificationCall.InOrder
            else -> null
        }
    }
}

internal object MockitoKotlinVerificationCallMatcher : VerificationCallMatcher {
    override fun match(owner: String, name: String, descriptor: String): VerificationCall? {
        if (!owner.startsWith("org/mockito/kotlin/")) {
            return null
        }

        return when (name) {
            "verify" -> VerificationCall.Verify
            "verifyNoInteractions" -> VerificationCall.VerifyNoInteractions
            "verifyNoMoreInteractions" -> VerificationCall.VerifyNoMoreInteractions
            else -> null
        }
    }
}

internal class CompositeVerificationCallMatcher(
    private val matchers: List<VerificationCallMatcher> = listOf(
        MockitoVerificationCallMatcher,
        MockitoKotlinVerificationCallMatcher,
    ),
) : VerificationCallMatcher {
    override fun match(owner: String, name: String, descriptor: String): VerificationCall? =
        matchers.firstNotNullOfOrNull { it.match(owner, name, descriptor) }
}
