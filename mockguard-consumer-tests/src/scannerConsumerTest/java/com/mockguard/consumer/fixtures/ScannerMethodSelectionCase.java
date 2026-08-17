package com.mockguard.consumer.fixtures;

import com.mockguard.GuardedMock;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.verify;

public class ScannerMethodSelectionCase {

    @GuardedMock
    @Mock
    private List<String> service;

    @Mock
    private List<String> unguardedService;

    public void verified() {
        service.size();
        unguardedService.size();
        verify(service).size();
    }

    public void unverified() {
        service.size();
        unguardedService.size();
    }
}
