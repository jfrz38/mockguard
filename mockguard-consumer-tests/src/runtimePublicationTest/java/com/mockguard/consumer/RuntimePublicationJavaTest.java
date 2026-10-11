package com.mockguard.consumer;

import com.mockguard.MockGuard;
import com.mockguard.StrictMode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.verify;

@MockGuard(mode = StrictMode.FAIL)
class RuntimePublicationJavaTest {
    @Mock
    private List<String> dependency;

    @Test
    void executesJavaConsumerAgainstPublishedRuntime() {
        dependency.size();

        verify(dependency).size();
    }
}
