package com.mockguard.integration.fixtures

import com.mockguard.GuardedMock
import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify

@MockGuard(mode = StrictMode.FAIL)
class NoGuardedMocksFallbackCase {
    @Mock
    lateinit var paymentGateway: Dependency

    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        paymentGateway.call()
        verify(paymentGateway).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class GuardedMockVerificationCase {
    @GuardedMock
    @Mock
    lateinit var paymentGateway: Dependency

    @Mock
    lateinit var logger: Logger

    @Test
    fun passes() {
        paymentGateway.call()
        logger.info("not guarded")

        verify(paymentGateway).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class GuardedMockFailureCase {
    @GuardedMock
    @Mock
    lateinit var paymentGateway: Dependency

    @Mock
    lateinit var logger: Logger

    @Test
    fun fails() {
        paymentGateway.call()
        logger.info("not guarded")
    }
}

@MockGuard(mode = StrictMode.FAIL)
class TwoGuardedMocksVerificationCase {
    @GuardedMock
    @Mock
    lateinit var paymentGateway: Dependency

    @GuardedMock
    @Mock
    lateinit var auditLogger: Logger

    @Mock
    lateinit var secondaryLogger: Logger

    @Test
    fun passes() {
        paymentGateway.call()
        auditLogger.info("guarded")
        secondaryLogger.info("not guarded")

        verify(paymentGateway).call()
        verify(auditLogger).info("guarded")
    }
}

@MockGuard(mode = StrictMode.FAIL)
class TwoGuardedMocksFailureCase {
    @GuardedMock
    @Mock
    lateinit var paymentGateway: Dependency

    @GuardedMock
    @Mock
    lateinit var auditLogger: Logger

    @Mock
    lateinit var secondaryLogger: Logger

    @Test
    fun fails() {
        paymentGateway.call()
        auditLogger.info("guarded")
        secondaryLogger.info("not guarded")

        verify(paymentGateway).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class AllMocksGuardedCase {
    @GuardedMock
    @Mock
    lateinit var paymentGateway: Dependency

    @GuardedMock
    @Mock
    lateinit var auditLogger: Logger

    @Test
    fun passes() {
        paymentGateway.call()
        auditLogger.info("guarded")

        verify(paymentGateway).call()
        verify(auditLogger).info("guarded")
    }
}
