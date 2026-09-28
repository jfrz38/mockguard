package com.mockguard.scanner.model

internal data class MockField(
    val name: String,
    val descriptor: String,
    val isSpy: Boolean = false,
    val isIgnored: Boolean = false,
    val isGuarded: Boolean = false,
)
