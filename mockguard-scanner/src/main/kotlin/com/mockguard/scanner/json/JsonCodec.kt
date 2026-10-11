package com.mockguard.scanner.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal object JsonCodec {
    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()
}
