package com.mockguard.integration.fixtures

import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.Test
import org.mockito.Mock

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
