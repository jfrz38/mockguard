package com.mockguard.scanner.fixtures

import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.verify as kotlinVerify
import org.mockito.kotlin.verifyNoInteractions as kotlinVerifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions as kotlinVerifyNoMoreInteractions

class KotlinDirectMockitoVerificationFixture {
    @Mock
    lateinit var dependency: KotlinScannerDependency

    fun test() {
        dependency.call()
        Mockito.verify(dependency).call()
    }
}

class KotlinMockitoKotlinVerificationFixture {
    @Mock
    lateinit var dependency: KotlinScannerDependency

    fun test() {
        dependency.call()
        kotlinVerify(dependency).call()
    }
}

class KotlinMockitoKotlinNoInteractionsFixture {
    @Mock
    lateinit var dependency: KotlinScannerDependency

    fun test() {
        kotlinVerifyNoInteractions(dependency)
    }
}

class KotlinMockitoKotlinNoMoreInteractionsFixture {
    @Mock
    lateinit var dependency: KotlinScannerDependency

    fun test() {
        dependency.call()
        kotlinVerify(dependency).call()
        kotlinVerifyNoMoreInteractions(dependency)
    }
}

class KotlinCustomVerifyHelperFixture {
    @Mock
    lateinit var dependency: KotlinScannerDependency

    fun test() {
        verify(dependency)
    }

    private fun verify(dependency: KotlinScannerDependency) {
        dependency.toString()
    }
}

class KotlinBacktickMethodFixture {
    @Mock
    lateinit var dependency: KotlinScannerDependency

    fun `unverified method`() {
        dependency.call()
    }
}

interface KotlinScannerDependency {
    fun call()
}
