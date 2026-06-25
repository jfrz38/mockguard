package com.mockguard.consumer.fixtures;

import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@MockGuard(mode = StrictMode.FAIL)
public class JavaVerifiedMockCase {
    @Mock
    Dependency dependency;

    @Test
    void passes() {
        dependency.call();

        verify(dependency).call();
    }

    interface Dependency {
        void call();
    }
}
