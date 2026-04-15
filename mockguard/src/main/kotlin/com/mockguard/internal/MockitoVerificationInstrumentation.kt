package com.mockguard.internal

import net.bytebuddy.ByteBuddy
import net.bytebuddy.asm.Advice
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.matcher.ElementMatchers.isStatic
import net.bytebuddy.matcher.ElementMatchers.named
import net.bytebuddy.agent.ByteBuddyAgent
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicBoolean

internal object MockitoVerificationInstrumentation {
    private val installed = AtomicBoolean(false)
    private val attempted = AtomicBoolean(false)

    fun ensureInstalled() {
        if (installed.get()) {
            return
        }

        synchronized(this) {
            if (installed.get() || attempted.get()) {
                return
            }

            attempted.set(true)

            ByteBuddyAgent.install()

            ByteBuddy()
                .redefine(Mockito::class.java)
                .visit(
                    Advice.to(VerifyNoInteractionsAdvice::class.java)
                        .on(named<MethodDescription>("verifyNoInteractions").and(isStatic())),
                )
                .visit(
                    Advice.to(VerifyNoMoreInteractionsAdvice::class.java)
                        .on(named<MethodDescription>("verifyNoMoreInteractions").and(isStatic())),
                )
                .make()
                .load(
                    Mockito::class.java.classLoader,
                    ClassReloadingStrategy.fromInstalledAgent(),
                )

            installed.set(true)
        }
    }

    @Suppress("unused")
    object VerifyNoInteractionsAdvice {
        @JvmStatic
        @Advice.OnMethodExit(onThrowable = Throwable::class)
        fun exit(
            @Advice.Argument(0) mocks: Array<Any?>?,
            @Advice.Thrown throwable: Throwable?,
        ) {
            if (throwable == null && mocks != null) {
                MockTracker.markVerified(*mocks)
            }
        }
    }

    @Suppress("unused")
    object VerifyNoMoreInteractionsAdvice {
        @JvmStatic
        @Advice.OnMethodExit(onThrowable = Throwable::class)
        fun exit(
            @Advice.Argument(0) mocks: Array<Any?>?,
            @Advice.Thrown throwable: Throwable?,
        ) {
            if (throwable == null && mocks != null) {
                MockTracker.markVerified(*mocks)
            }
        }
    }
}
