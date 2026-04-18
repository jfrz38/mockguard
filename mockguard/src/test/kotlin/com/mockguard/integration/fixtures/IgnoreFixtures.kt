package com.mockguard.integration.fixtures

import com.mockguard.MockGuard
import com.mockguard.MockGuardIgnore
import com.mockguard.MockGuards
import com.mockguard.StrictMode
import org.junit.jupiter.api.Test
import org.mockito.Mock

@MockGuard(mode = StrictMode.FAIL)
class IgnoredMockCase {
    @Mock
    @MockGuardIgnore
    lateinit var logger: Logger

    @Test
    fun passes() {
        logger.info("ignored")
    }
}

@MockGuard(mode = StrictMode.FAIL)
class ProgrammaticIgnoreCase {
    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        MockGuards.ignore(logger)
        logger.info("ignored programmatically")
    }
}
