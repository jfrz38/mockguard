package com.mockguard.integration.fixtures

import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions

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
class ZeroInteractionVerificationCase {
    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        verifyNoInteractions(logger)
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

@MockGuard(mode = StrictMode.FAIL)
class VerifiedInAfterEachCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun passes() {
        dependency.call()
    }

    @AfterEach
    fun verifyAfterEach() {
        verify(dependency).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class NoInteractionsVerifiedInAfterEachCase {
    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        // Intentionally left unused and verified in @AfterEach.
    }

    @AfterEach
    fun verifyAfterEach() {
        verifyNoInteractions(logger)
    }
}
