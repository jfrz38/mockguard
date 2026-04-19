package com.mockguard.integration.fixtures

import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.Mockito.inOrder
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

@MockGuard(mode = StrictMode.FAIL)
class SpyVerificationCase {
    @Spy
    lateinit var spyTarget: SpyTarget

    @Test
    fun passes() {
        spyTarget.call()
        verify(spyTarget).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class InjectMocksVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @InjectMocks
    lateinit var service: InjectedService

    @Test
    fun passes() {
        service.process()
        verify(dependency).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class InOrderVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun passes() {
        dependency.call()
        inOrder(dependency).verify(dependency).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class ParameterizedVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @ParameterizedTest
    @ValueSource(strings = ["a", "b"])
    fun passes(input: String) {
        dependency.call()
        verify(dependency).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class RepeatedVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @RepeatedTest(2)
    fun passes() {
        dependency.call()
        verify(dependency).call()
    }
}
