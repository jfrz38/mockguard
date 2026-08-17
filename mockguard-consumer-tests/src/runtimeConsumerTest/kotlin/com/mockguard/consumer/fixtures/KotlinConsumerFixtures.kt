package com.mockguard.consumer.fixtures

import com.mockguard.MockGuard
import com.mockguard.StrictMode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.verify as kotlinVerify
import org.mockito.kotlin.verifyNoInteractions as kotlinVerifyNoInteractions

@MockGuard(mode = StrictMode.FAIL)
class KotlinAfterEachVerificationCase {
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
class KotlinParameterizedVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @ParameterizedTest
    @ValueSource(strings = ["a", "b"])
    fun passes(input: String) {
        dependency.call(input)
        verify(dependency).call(input)
    }
}

@MockGuard(mode = StrictMode.FAIL)
class KotlinMockitoKotlinVerificationCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun passes() {
        dependency.call()

        kotlinVerify(dependency).call()
    }
}

@MockGuard(mode = StrictMode.FAIL)
class KotlinMockitoKotlinNoInteractionsCase {
    @Mock
    lateinit var dependency: Dependency

    @Test
    fun passes() {
        kotlinVerifyNoInteractions(dependency)
    }
}

interface Dependency {
    fun call()

    fun call(input: String)
}
