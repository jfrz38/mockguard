package com.mockguard

import com.mockguard.internal.MockTracker

object MockGuards {
    fun ignore(mock: Any) {
        MockTracker.ignore(mock)
    }

    fun register(mock: Any) {
        MockTracker.register(mock)
    }
}
