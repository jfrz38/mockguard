package com.mockguard.consumer.fixtures;

import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockGuard(mode = StrictMode.FAIL)
public class JavaVerifyNoMoreInteractionsCase {
    @Mock
    Dependency dependency;

    @Test
    void passes() {
        dependency.call();

        verify(dependency).call();
        verifyNoMoreInteractions(dependency);
    }

    interface Dependency {
        void call();
    }
}
