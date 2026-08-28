package com.mockguard.consumer.fixtures;

import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@MockGuard(mode = StrictMode.FAIL)
public class JavaUnverifiedMockCase {
    @Mock
    Dependency dependency;

    @Test
    void fails() {
        dependency.call();
    }

    interface Dependency {
        void call();
    }
}
