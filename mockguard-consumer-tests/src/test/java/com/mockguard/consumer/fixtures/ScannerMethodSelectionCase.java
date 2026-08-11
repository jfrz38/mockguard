package com.mockguard.consumer.fixtures;

import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.verify;

public class ScannerMethodSelectionCase {

    @Mock
    private List<String> service;

    public void verified() {
        service.size();
        verify(service).size();
    }

    public void unverified() {
        service.size();
    }
}
