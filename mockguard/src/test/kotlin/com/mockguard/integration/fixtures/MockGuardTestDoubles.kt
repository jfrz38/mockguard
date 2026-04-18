package com.mockguard.integration.fixtures

interface Dependency {
    fun call()
}

interface Logger {
    fun info(message: String)
}
