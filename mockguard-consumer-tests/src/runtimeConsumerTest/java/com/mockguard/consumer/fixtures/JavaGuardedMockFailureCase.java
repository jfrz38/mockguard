package com.mockguard.consumer.fixtures;

import com.mockguard.GuardedMock;
import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@MockGuard(mode = StrictMode.FAIL)
public class JavaGuardedMockFailureCase {
    @GuardedMock
    @Mock
    Dependency guardedDependency;

    @GuardedMock
    @Mock
    Dependency otherGuardedDependency;

    @Mock
    Dependency unguardedDependency;

    @Test
    void fails() {
        guardedDependency.call();
        otherGuardedDependency.call();
        unguardedDependency.call();

        verify(guardedDependency).call();
    }

    interface Dependency {
        void call();
    }
}
