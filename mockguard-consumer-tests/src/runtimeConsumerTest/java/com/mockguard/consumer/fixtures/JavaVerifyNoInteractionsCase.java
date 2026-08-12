package com.mockguard.consumer.fixtures;

import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verifyNoInteractions;

@MockGuard(mode = StrictMode.FAIL)
public class JavaVerifyNoInteractionsCase {
    @Mock
    Dependency dependency;

    @Test
    void passes() {
        verifyNoInteractions(dependency);
    }

    interface Dependency {
        void call();
    }
}
