package com.mockguard.scanner.model

data class MockField(
    val name: String,
    val descriptor: String,
    val isSpy: Boolean = false,
    val isIgnored: Boolean = false,
    val isGuarded: Boolean = false,
)
