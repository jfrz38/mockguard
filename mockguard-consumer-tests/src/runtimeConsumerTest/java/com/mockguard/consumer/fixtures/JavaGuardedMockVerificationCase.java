package com.mockguard.consumer.fixtures;

import com.mockguard.GuardedMock;
import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@MockGuard(mode = StrictMode.FAIL)
public class JavaGuardedMockVerificationCase {
    @GuardedMock
    @Mock
    Dependency guardedDependency;

    @Mock
    Dependency unguardedDependency;

    @Test
    void passes() {
        guardedDependency.call();
        unguardedDependency.call();

        verify(guardedDependency).call();
    }

    interface Dependency {
        void call();
    }
}
