package com.mockguard.consumer

import com.mockguard.consumer.fixtures.JavaGuardedMockFailureCase
import com.mockguard.consumer.fixtures.JavaGuardedMockVerificationCase
import com.mockguard.consumer.fixtures.JavaMockitoExtensionCase
import com.mockguard.consumer.fixtures.JavaUnverifiedMockCase
import com.mockguard.consumer.fixtures.JavaVerifiedMockCase
import com.mockguard.consumer.fixtures.JavaVerifyNoInteractionsCase
import com.mockguard.consumer.fixtures.JavaVerifyNoMoreInteractionsCase
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaConsumerIntegrationTest {

    @Test
    fun `Java verified mock passes`() {
        assertConsumerTestPasses(JavaVerifiedMockCase::class.java)
    }

    @Test
    fun `Java verifyNoInteractions passes`() {
        assertConsumerTestPasses(JavaVerifyNoInteractionsCase::class.java)
    }

    @Test
    fun `Java verifyNoMoreInteractions passes`() {
        assertConsumerTestPasses(JavaVerifyNoMoreInteractionsCase::class.java)
    }

    @Test
    fun `Java guarded mock ignores unguarded mocks`() {
        assertConsumerTestPasses(JavaGuardedMockVerificationCase::class.java)
    }

    @Test
    fun `Java MockitoExtension integration passes`() {
        assertConsumerTestPasses(JavaMockitoExtensionCase::class.java)
    }

    @Test
    fun `Java unverified mock fails`() {
        assertConsumerTestFails(JavaUnverifiedMockCase::class.java)
    }

    @Test
    fun `Java guarded mock without verification fails`() {
        assertConsumerTestFails(JavaGuardedMockFailureCase::class.java)
    }

    private fun assertConsumerTestPasses(testClass: Class<*>) {
        val summary = runConsumerTestClass(testClass)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    private fun assertConsumerTestFails(testClass: Class<*>) {
        val summary = runConsumerTestClass(testClass)

        assertEquals(0, summary.testsSucceededCount)
        assertEquals(1, summary.testsFailedCount)
    }
}
