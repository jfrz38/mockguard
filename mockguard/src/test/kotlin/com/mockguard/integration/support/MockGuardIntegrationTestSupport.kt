package com.mockguard.integration.support

import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.platform.launcher.listeners.TestExecutionSummary
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal object MockGuardIntegrationTestSupport {
    fun runTestClass(testClass: Class<*>): TestExecutionSummary =
        SummaryGeneratingListener().also { listener ->
            val request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build()

            LauncherFactory.create().apply {
                registerTestExecutionListeners(listener)
                execute(request)
            }
        }.summary

    fun captureStandardError(
        stream: ByteArrayOutputStream,
        block: () -> TestExecutionSummary,
    ): TestExecutionSummary {
        val original = System.err
        val capturing = PrintStream(stream, true)

        return try {
            System.setErr(capturing)
            block()
        } finally {
            capturing.flush()
            System.setErr(original)
        }
    }
}
