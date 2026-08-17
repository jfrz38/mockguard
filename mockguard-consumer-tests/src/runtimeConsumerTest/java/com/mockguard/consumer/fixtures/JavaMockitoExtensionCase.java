package com.mockguard.consumer.fixtures;

import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@MockGuard(mode = StrictMode.FAIL)
@ExtendWith(MockitoExtension.class)
public class JavaMockitoExtensionCase {
    @Mock
    Dependency dependency;

    @Test
    void passesWhenMockitoExtensionInitializesMocks() {
        dependency.call();

        verify(dependency).call();
    }

    interface Dependency {
        void call();
    }
}
