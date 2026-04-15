package com.mockguard.internal

import com.mockguard.StrictMode
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.listeners.VerificationListener
import org.mockito.listeners.VerificationStartedEvent
import org.mockito.listeners.VerificationStartedListener
import org.mockito.listeners.MockitoListener
import org.mockito.verification.VerificationEvent
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.Collections
import java.util.IdentityHashMap

internal object MockTracker {

    private val currentSession = ThreadLocal<Session?>()

    fun start(testInstance: Any): SessionHandle {
        val openMocksHandle = openMocksIfNeeded(testInstance)
        val session = Session(testInstance)
        session.start()
        currentSession.set(session)

        return SessionHandle(session::close, openMocksHandle)
    }

    fun verify(mode: StrictMode) {
        currentSession.get()?.verify(mode)
    }

    fun finish(handle: SessionHandle?) {
        try {
            handle?.close()
        } finally {
            currentSession.remove()
        }
    }

    fun ignore(mock: Any) {
        currentSession.get()?.ignore(mock)
            ?: error("MockGuard.ignore(...) can only be used while a MockGuard-managed test is running.")
    }

    fun register(mock: Any) {
        currentSession.get()?.register(mock)
            ?: error("MockGuard.register(...) can only be used while a MockGuard-managed test is running.")
    }

    private fun openMocksIfNeeded(testInstance: Any): AutoCloseable? {
        val needsInitialization = allFields(testInstance.javaClass)
            .filter { it.isAnnotationPresent(Mock::class.java) || it.isAnnotationPresent(Spy::class.java) }
            .any { field ->
                field.isAccessible = true
                field.get(testInstance) == null
            }

        return if (needsInitialization) MockitoAnnotations.openMocks(testInstance) else null
    }

    private fun allFields(type: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var current: Class<*>? = type

        while (current != null && current != Any::class.java) {
            fields += current.declaredFields
            current = current.superclass
        }

        return fields
    }

    internal class SessionHandle(
        private val closeSession: () -> Unit,
        private val openMocksHandle: AutoCloseable?,
    ) : AutoCloseable {
        override fun close() {
            try {
                closeSession()
            } finally {
                openMocksHandle?.close()
            }
        }
    }

    private class Session(private val testInstance: Any) : AutoCloseable {
        private val trackedMocks = LinkedHashMap<Any, String>()
        private val ignoredMocks = Collections.newSetFromMap(IdentityHashMap<Any, Boolean>())
        private val verifiedMocks = Collections.newSetFromMap(IdentityHashMap<Any, Boolean>())

        private val verificationListener = object : VerificationListener {
            override fun onVerification(event: VerificationEvent) {
                verifiedMocks += event.mock
            }
        }
        private val verificationStartedListener = object : VerificationStartedListener, MockitoListener {
            override fun onVerificationStarted(event: VerificationStartedEvent) {
                verifiedMocks += event.mock
            }
        }

        fun start() {
            discoverMocks()
            Mockito.framework().addListener(verificationListener)
            Mockito.framework().addListener(verificationStartedListener)
        }

        fun register(mock: Any) {
            if (!isTrackableMock(mock)) {
                error("Only Mockito mocks or spies can be registered with MockGuard.")
            }

            trackedMocks.putIfAbsent(mock, describeMock(mock))
        }

        fun ignore(mock: Any) {
            register(mock)
            ignoredMocks += mock
        }

        fun verify(mode: StrictMode) {
            if (mode == StrictMode.OFF) {
                return
            }

            val invalidMocks = trackedMocks.entries
                .filter { (mock, _) -> mock !in ignoredMocks && !isVerified(mock) }
                .map { (mock, label) -> buildViolation(mock, label) }

            if (invalidMocks.isEmpty()) {
                return
            }

            val message = buildString {
                appendLine("[MockGuard] Found ${invalidMocks.size} unverified mock(s).")
                invalidMocks.forEach { appendLine("- $it") }
                append("Verify each mock explicitly or opt out with @MockGuardIgnore / MockGuards.ignore(mock).")
            }

            when (mode) {
                StrictMode.WARN -> System.err.println(message)
                StrictMode.FAIL -> throw AssertionError(message)
                StrictMode.OFF -> Unit
            }
        }

        override fun close() {
            Mockito.framework().removeListener(verificationListener)
            Mockito.framework().removeListener(verificationStartedListener)
        }

        private fun discoverMocks() {
            allFields(testInstance.javaClass)
                .asSequence()
                .filterNot { Modifier.isStatic(it.modifiers) }
                .onEach { it.isAccessible = true }
                .forEach { field ->
                    val value = field.get(testInstance) ?: return@forEach
                    if (!shouldTrack(field, value)) {
                        return@forEach
                    }

                    trackedMocks.putIfAbsent(value, field.name)
                    if (field.isAnnotationPresent(com.mockguard.MockGuardIgnore::class.java)) {
                        ignoredMocks += value
                    }
                }
        }

        private fun shouldTrack(field: Field, value: Any): Boolean {
            if (field.isAnnotationPresent(Mock::class.java) || field.isAnnotationPresent(Spy::class.java)) {
                return true
            }

            return isTrackableMock(value)
        }

        private fun isVerified(mock: Any): Boolean {
            val invocations = Mockito.mockingDetails(mock).invocations
            return mock in verifiedMocks || invocations.any { it.isVerified }
        }

        private fun buildViolation(mock: Any, label: String): String {
            val invocations = Mockito.mockingDetails(mock).invocations
            val mockType = describeMock(mock)

            return if (invocations.isEmpty()) {
                "$label ($mockType) was never verified. Use verifyNoInteractions($label) if the mock is intentionally unused."
            } else {
                "$label ($mockType) had ${invocations.size} invocation(s) but was never verified."
            }
        }

        private fun describeMock(mock: Any): String = try {
            Mockito.mockingDetails(mock).mockCreationSettings.typeToMock.simpleName
        } catch (_: Exception) {
            mock.javaClass.simpleName
        }

        private fun isTrackableMock(value: Any): Boolean {
            val details = Mockito.mockingDetails(value)
            return details.isMock || details.isSpy
        }
    }
}
