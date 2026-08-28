package com.mockguard.scanner

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MainTest {

    @Test
    fun `parses repeated test selectors`() {
        val config = parseArgs(
            arrayOf(
                "--class-dir=build/classes/java/test",
                "--test=com.example.OrderTest#createsOrder",
                "--test",
                "com.example.OrderTest#createsOrder(I)V",
            ),
        )

        assertEquals(2, config?.tests?.size)
        assertEquals("com.example.OrderTest", config?.tests?.first()?.className)
        assertEquals("createsOrder", config?.tests?.first()?.methodName)
        assertEquals("(I)V", config?.tests?.last()?.methodDescriptor)
    }

    @Test
    fun `deduplicates repeated test selectors`() {
        val config = parseArgs(
            arrayOf(
                "--class-dir=classes",
                "--test=example.MyTest#case",
                "--test=example.MyTest#case",
            ),
        )

        assertEquals(1, config?.tests?.size)
    }

    @Test
    fun `rejects malformed test selectors`() {
        assertFailsWith<IllegalArgumentException> {
            parseArgs(arrayOf("--class-dir=classes", "--test=example.MyTest"))
        }
        assertFailsWith<IllegalArgumentException> {
            parseArgs(arrayOf("--class-dir=classes", "--test=example.MyTest#case(bad)"))
        }
    }
}
