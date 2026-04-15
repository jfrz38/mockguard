package com.mockguard

import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ExtendWith(MockGuardExtension::class)
annotation class MockGuard(
    val mode: StrictMode = StrictMode.FAIL
)
