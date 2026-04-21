package com.mockguard.integration.fixtures

interface Dependency {
    fun call()
}

interface Logger {
    fun info(message: String)
}

open class SpyTarget {
    open fun call() = "ok"
}

class InjectedService(private val dependency: Dependency) {
    fun process() {
        dependency.call()
    }
}
