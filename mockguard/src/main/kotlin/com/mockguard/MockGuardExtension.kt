package com.mockguard

import com.mockguard.internal.MockTracker
import com.mockguard.internal.MockitoVerificationInstrumentation
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class MockGuardExtension : BeforeEachCallback, AfterEachCallback {
    private companion object {
        val namespace = ExtensionContext.Namespace.create(MockGuardExtension::class.java)
        const val sessionKey = "mockguard.session"
    }

    override fun beforeEach(context: ExtensionContext) {
        MockitoVerificationInstrumentation.ensureInstalled()
        val handle = MockTracker.start(context.requiredTestInstance)
        context.getStore(namespace).put(sessionKey, handle)
    }

    override fun afterEach(context: ExtensionContext) {
        val mode = context.requiredTestClass
            .getAnnotation(MockGuard::class.java)
            ?.mode ?: StrictMode.OFF

        val handle = context.getStore(namespace).remove(sessionKey, MockTracker.SessionHandle::class.java)

        try {
            MockTracker.verify(mode)
        } finally {
            MockTracker.finish(handle)
        }
    }
}
