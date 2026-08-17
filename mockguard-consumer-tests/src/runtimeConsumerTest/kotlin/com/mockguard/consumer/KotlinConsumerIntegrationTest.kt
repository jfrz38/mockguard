package com.mockguard.consumer

import com.mockguard.consumer.fixtures.KotlinAfterEachVerificationCase
import com.mockguard.consumer.fixtures.KotlinParameterizedVerificationCase
import com.mockguard.consumer.fixtures.KotlinMockitoKotlinNoInteractionsCase
import com.mockguard.consumer.fixtures.KotlinMockitoKotlinVerificationCase
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinConsumerIntegrationTest {

    @Test
    fun `Kotlin afterEach verification passes from consumer module`() {
        val summary = runConsumerTestClass(KotlinAfterEachVerificationCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun `Kotlin parameterized verification passes from consumer module`() {
        val summary = runConsumerTestClass(KotlinParameterizedVerificationCase::class.java)

        assertEquals(2, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun `Kotlin mockito-kotlin verification passes from consumer module`() {
        val summary = runConsumerTestClass(KotlinMockitoKotlinVerificationCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }

    @Test
    fun `Kotlin mockito-kotlin verifyNoInteractions passes from consumer module`() {
        val summary = runConsumerTestClass(KotlinMockitoKotlinNoInteractionsCase::class.java)

        assertEquals(1, summary.testsSucceededCount)
        assertEquals(0, summary.testsFailedCount)
    }
}
