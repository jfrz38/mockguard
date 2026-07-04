package com.mockguard.consumer

import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.platform.launcher.listeners.TestExecutionSummary

internal fun runConsumerTestClass(testClass: Class<*>): TestExecutionSummary =
    SummaryGeneratingListener().also { listener ->
        val request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(testClass))
            .build()

        LauncherFactory.create().apply {
            registerTestExecutionListeners(listener)
            execute(request)
        }
    }.summary
